package controllers

import (
	"encoding/base64"
	"errors"
	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"go.mongodb.org/mongo-driver/bson"
	"math"
	"net/http"
	"server/db"
	"server/helpers"
	"server/models"
)

func Purchase(context *gin.Context) {
	purchaseCollection := db.GetDatabase().Collection("purchases")
	usersCollection := db.GetDatabase().Collection("users")

	signedMessage, err := models.CreateSignedMessageFromContext(context)
	if err != nil {
		helpers.SetStatusBadRequest(context, "error parsing signed message")
		return
	}

	messageBytes, err := base64.StdEncoding.Strict().DecodeString(signedMessage.B64MessageString)
	if err != nil {
		helpers.SetStatusBadRequest(context, "error decoding message: "+err.Error())
		return
	}

	purchaseJson := string(messageBytes)
	purchase, err := models.CreatePurchaseFromJSON(purchaseJson)
	if err != nil {
		helpers.SetStatusBadRequest(context, "error parsing json message: "+err.Error())
		return
	}

	var user models.User
	if err := usersCollection.FindOne(context, bson.M{"uuid": purchase.UserUUID}).Decode(&user); err != nil {
		helpers.SetStatusBadRequest(context, "user with provided uuid does not exist")
		return
	}

	if err := verifyActiveCoupon(purchase, user); err != nil {
		helpers.SetStatusBadRequest(context, "error message: "+err.Error())
		return
	}

	totalPrice, paidPrice, err := payment(context, purchase, user)
	if err != nil {
		helpers.SetStatusBadRequest(context, "error payment message: "+err.Error())
		return
	}

	updatedValues := bson.M{"accumulatedvalue": user.AccumulatedValue, "accumulatedpaidvalue": user.AccumulatedPaidValue}
	if _, err = usersCollection.UpdateOne(context, bson.M{"_id": user.ID}, bson.M{"$set": updatedValues}); err != nil {
		helpers.SetStatusInternalServerError(context, "error updating user message: "+err.Error())
		return
	}

	if _, err := purchaseCollection.InsertOne(context, purchase); err != nil {
		helpers.SetStatusInternalServerError(context, "error inserting user message: "+err.Error())
		return
	}
	context.JSON(http.StatusOK, gin.H{"total_value": totalPrice, "paid_value": paidPrice})
}

func verifyActiveCoupon(purchase *models.Purchase, user models.User) error {
	if purchase.Coupon == nil {
		return nil
	}

	for _, coupon := range user.ActiveCoupons {
		if coupon == *purchase.Coupon {
			return nil
		}
	}
	return errors.New("invalid coupon")
}

func removeCoupon(coupon uuid.UUID, user models.User) {
	index := 0
	for i, activeCoupon := range user.ActiveCoupons {
		if activeCoupon == coupon {
			index = i
		}
	}
	user.ActiveCoupons = append(user.ActiveCoupons[:index], user.ActiveCoupons[index+1:]...)
}

func addCoupon(user models.User) {
	user.ActiveCoupons = append(user.ActiveCoupons, uuid.New())
}

func payment(context *gin.Context, purchase *models.Purchase, user models.User) (totalPrice float64, paidPrice float64, err error) {
	totalPrice = 0
	paidPrice = 0
	err = nil

	productCollection := db.GetDatabase().Collection("products")
	for _, cardProduct := range purchase.Cart {
		var product models.Product
		if doc := productCollection.FindOne(context, bson.M{"uuid": cardProduct.ProductUUID}); doc != nil {
			err = doc.Err()
			return
		}
		totalPrice += product.Price * float64(cardProduct.Quantity)
	}

	if purchase.Discount {
		paidPrice = math.Max(0, totalPrice-user.AccumulatedValue)
		user.AccumulatedValue = math.Max(0, user.AccumulatedValue-totalPrice)
	}

	if purchase.Coupon != nil {
		user.AccumulatedValue += paidPrice * 0.15
		removeCoupon(*purchase.Coupon, user)
	}
	previousAccumulatedPaidValue := user.AccumulatedPaidValue
	user.AccumulatedPaidValue += paidPrice

	newCopounsNumber := int(user.AccumulatedPaidValue/100) - int(previousAccumulatedPaidValue/100)
	for i := 0; i < newCopounsNumber; i++ {
		addCoupon(user)
	}
	return
}
