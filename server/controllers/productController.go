package controllers

import (
	"encoding/base64"
	"encoding/json"
	"fmt"
	"net/http"
	"server/db"
	"server/helpers"
	"server/models"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

func CreateOrReplaceProduct(context *gin.Context) {
	productsCollection := db.GetDatabase().Collection("products")

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

	message := string(messageBytes)
	doc, err := models.CreateProductFromJSON(message)
	if err != nil {
		helpers.SetStatusBadRequest(context, err.Error())
		return
	}

	doc.UUID = uuid.New()
	sr := productsCollection.FindOneAndReplace(context, bson.M{"name": doc.Name}, doc, options.FindOneAndReplace().SetUpsert(true))
	if sr.Err() != nil && sr.Err() != mongo.ErrNoDocuments {
		helpers.SetStatusInternalServerError(context, "error inserting product into collection")
		return
	}

	context.JSON(http.StatusOK, doc)
}

func DeleteProduct(context *gin.Context) {
	productUUID, err := uuid.Parse(context.Param("uuid"))
	if err != nil {
		helpers.SetStatusBadRequest(context, "cannot parse uuid")
		return
	}

	productsCollection := db.GetDatabase().Collection("products")

	_, err = productsCollection.DeleteOne(context, bson.M{"uuid": productUUID})
	if err != nil {
		helpers.SetStatusInternalServerError(context,
			"error deleting product from collection: "+err.Error())
		return
	}

	context.Status(http.StatusNoContent)
}

func GetProduct(context *gin.Context) {
	productsCollection := db.GetDatabase().Collection("products")

	productUUID, err := uuid.Parse(context.Param("uuid"))
	if err != nil {
		helpers.SetStatusBadRequest(context, "cannot parse uuid")
		return
	}

	doc := productsCollection.FindOne(context, bson.M{"uuid": productUUID})
	if doc.Err() != nil {
		context.JSON(http.StatusNotFound, doc.Err().Error())
		return
	}

	var product models.Product
	doc.Decode(&product)

	encryptedUUID, err := helpers.EncryptMessage([]byte(product.UUID.String()))
	if err != nil {
		helpers.SetStatusInternalServerError(context, "error encrypting product UUID: "+err.Error())
		return
	}

	encryptedName, err := helpers.EncryptMessage([]byte(product.Name))
	if err != nil {
		helpers.SetStatusInternalServerError(context, "error encrypting product name: "+err.Error())
		return
	}

	encryptedPrice, err := helpers.EncryptMessage([]byte(fmt.Sprintf("%.2f", product.Price)))
	if err != nil {
		helpers.SetStatusInternalServerError(context, "error encrypting product price: "+err.Error())
		return
	}

	encryptedProduct, _ := json.Marshal(models.EncryptedProduct{UUID: encryptedUUID, Name: encryptedName, Price: encryptedPrice})

	context.JSON(http.StatusOK, encryptedProduct)
}

func GetProducts(context *gin.Context) {
	productsCollection := db.GetDatabase().Collection("products")

	cursor, err := productsCollection.Find(context, bson.M{})
	if err != nil {
		helpers.SetStatusInternalServerError(context, "error retreiving products")
		return
	}

	var products []models.Product
	err = cursor.All(context, &products)
	if err != nil {
		helpers.SetStatusInternalServerError(context, "error retreiving products")
		return
	}

	if len(products) < 1 {
		context.Status(http.StatusNoContent)
		return
	}

	context.JSON(http.StatusOK, products)
}

func GetProductImage(context *gin.Context) {
	productsCollection := db.GetDatabase().Collection("products")

	productUUID, err := uuid.Parse(context.Param("uuid"))
	if err != nil {
		helpers.SetStatusBadRequest(context, "cannot parse uuid")
		return
	}

	doc := productsCollection.FindOne(context, bson.M{"uuid": productUUID})
	if doc.Err() != nil {
		context.JSON(http.StatusNotFound, doc.Err().Error())
		return
	}

	var product models.Product
	doc.Decode(&product)

	if product.Image == "" {
		context.Status(http.StatusNoContent)
		return
	}

	context.JSON(http.StatusOK, gin.H{"b64Image":product.Image})
}