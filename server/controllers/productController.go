package controllers

import (
	"encoding/base64"
	"encoding/json"
	"net/http"
	database "server/db"
	"server/helpers"
	"server/models"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

func CreateUpdateProduct(context *gin.Context) {
	db := database.GetDatabase()
	productsCollection := db.Collection("products")

	b64Message := context.MustGet("b64Message")

	encodedJSON := make([]byte, base64.StdEncoding.DecodedLen(len(b64Message.([]byte))))
	_, err := base64.StdEncoding.Decode(encodedJSON, b64Message.([]byte))
	if err != nil {
		helpers.SetStatusBadRequest(context, "invalid JSON encoding")
		return
	}

	doc, err := models.CreateProductFromJSONString(encodedJSON)
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

	context.Status(http.StatusNoContent)
}

func GetProduct(context *gin.Context) {
	db := database.GetDatabase()
	productsCollection := db.Collection("products")

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

	jsonProduct, _ := json.Marshal(product)
	b64Product := make([]byte, base64.StdEncoding.EncodedLen(len(jsonProduct)))
	base64.StdEncoding.Encode(b64Product, jsonProduct)

	encryptedProduct, err := helpers.EncryptMessage(b64Product)
	if err != nil {
		helpers.SetStatusInternalServerError(context, "error encrypting product")
		return
	}

	b64EncryptedProduct := make([]byte, base64.StdEncoding.EncodedLen(len(encryptedProduct)))
	base64.StdEncoding.Encode(b64EncryptedProduct, encryptedProduct)

	context.JSON(http.StatusOK, b64EncryptedProduct)
}

func GetProducts(context *gin.Context) {
	db := database.GetDatabase()
	productsCollection := db.Collection("products")

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
