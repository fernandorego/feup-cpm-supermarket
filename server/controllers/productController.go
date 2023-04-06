package controllers

import (
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

	doc, err := models.CreateProductFromJSON(context)
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

	context.JSON(http.StatusOK, product)
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
