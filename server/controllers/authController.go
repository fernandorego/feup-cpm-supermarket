package controllers

import (
	"encoding/base64"
	"net/http"
	"os"
	"server/db"
	"server/helpers"
	"server/models"

	"github.com/google/uuid"

	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

func Register(context *gin.Context) {
	database := db.GetDatabase()
	usersCollection := database.Collection("users")

	doc, err := models.CreateUserFromJSON(context)
	if err != nil {
		helpers.SetStatusBadRequest(context, err.Error())
		return
	}

	if !(helpers.IsUnique(context, usersCollection, bson.M{"nickname": doc.Nickname}, "this nickname already exists")) {
		return
	}

	doc.Password = helpers.HashPassword(doc.Password)
	doc.UUID = uuid.New()

	publicKey, err := base64.StdEncoding.DecodeString(doc.PublicKey)
	if err != nil {
		helpers.SetStatusBadRequest(context, "invalid public key encoding")
		return
	}

	doc.PublicKey = string(publicKey)

	res, err := usersCollection.InsertOne(context, doc)
	if err != nil {
		helpers.SetStatusInternalServerError(context, "error inserting user into collection")
		return
	}
	provideToken(context, res.InsertedID.(primitive.ObjectID), doc.IsAdmin, gin.H{"server_private_key": os.Getenv("PUBLIC_KEY")})

	println("In register", doc.PublicKey)
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
	if err = usersCollection.FindOne(context, bson.M{"nickname": credentials.Nickname}).Decode(&user); err != nil {
		helpers.SetStatusBadRequest(context, "user with provided nickname does not exist")
		return
	}

	if !helpers.CheckPasswordHash(credentials.Password, user.Password) {
		helpers.SetStatusBadRequest(context, "incorrect password")
		return
	}

	publicKey, err := base64.StdEncoding.DecodeString(credentials.PublicKey)
	if err != nil {
		helpers.SetStatusBadRequest(context, "invalid public key encoding")
		return
	}

	user.PublicKey = string(publicKey)
	usersCollection.FindOneAndReplace(context, bson.M{"nickname": user.Nickname}, user)
	provideToken(context, user.ID, user.IsAdmin, gin.H{"server_private_key": os.Getenv("PRIVATE_KEY")})

	println("In login", user.PublicKey)
}

func provideToken(context *gin.Context, id primitive.ObjectID, isAdmin bool, args map[string]interface{}) {
	token, err := helpers.GenerateToken(id, isAdmin)
	if err != nil {
		helpers.SetStatusInternalServerError(context, "error creating access token")
		return
	}
	json := gin.H{"access_token": token}
	for key, value := range args {
		json[key] = value
	}
	context.JSON(http.StatusOK, json)
}
