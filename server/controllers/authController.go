package controllers

import (
	"net/http"

	"server/db"
	"server/helpers"
	"server/models"

	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

func Register(context *gin.Context) {
	database := db.GetDatabase()
	usersCollection := database.Collection("users")

	doc, err := models.GetUserFromJSON(context)
	if err != nil {
		helpers.SetStatusBadRequest(context, err.Error())
		return
	}

	if !(helpers.IsUnique(context, usersCollection, bson.M{"email": doc.Email}, "this email already exists")) {
		return
	}

	doc.Password = helpers.HashPassword(doc.Password)

	res, err := usersCollection.InsertOne(context, doc)
	if err != nil {
		helpers.SetStatusInternalServerError(context, "error inserting user into collection")
		return
	}

	provideToken(context, res.InsertedID.(primitive.ObjectID))
	return
}

func GenerateToken(context *gin.Context) {
	database := db.GetDatabase()
	usersCollection := database.Collection("users")

	credentials, err := models.GetUserCredentialsFromJSON(context)
	if err != nil {
		helpers.SetStatusBadRequest(context, err.Error())
		return
	}

	var user models.User
	if err = usersCollection.FindOne(context, bson.M{"email": credentials.Email}).Decode(&user); err != nil {
		helpers.SetStatusBadRequest(context, "user with provided email does not exist")
		return
	}

	if !helpers.CheckPasswordHash(credentials.Password, user.Password) {
		helpers.SetStatusBadRequest(context, "incorrect password")
		return
	}

	provideToken(context, user.ID)
	return
}

func provideToken(context *gin.Context, id primitive.ObjectID) {
	token, err := helpers.GenerateToken(id)
	if err != nil {
		helpers.SetStatusInternalServerError(context, "error creating access token")
		return
	}
	context.JSON(http.StatusOK, gin.H{"access_token": token})
	return
}
