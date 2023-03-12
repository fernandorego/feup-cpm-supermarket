package controllers

import (
	"net/http"
	"time"

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

	if !(helpers.CheckUnique(context, usersCollection, bson.M{"email": context.PostForm("email")}, "this email already exists")) {
		return
	}

	doc := models.User{
		Name:      context.PostForm("name"),
		Email:     context.PostForm("email"),
		Password:  helpers.HashPassword(context.PostForm("password")),
		CreatedAt: time.Now(),
		UpdatedAt: time.Now(),
	}

	res, err := usersCollection.InsertOne(context, doc)
	if err != nil {
		context.JSON(http.StatusInternalServerError, gin.H{"error": "error inserting user into collection"})
		return
	}

	token, err := helpers.GenerateToken(res.InsertedID.(primitive.ObjectID))
	if err != nil {
		context.JSON(http.StatusInternalServerError, gin.H{"error": "error creating access token"})
		return
	}

	context.JSON(http.StatusOK, gin.H{"access_token": token})
	return
}

func GenerateToken(context *gin.Context) {
	database := db.GetDatabase()
	usersCollection := database.Collection("users")

	var user models.User
	err := usersCollection.FindOne(context, bson.M{"email": context.PostForm("email")}).Decode(&user)
	if err != nil {
		context.JSON(http.StatusBadRequest, gin.H{"error": "user with provided email does not exist"})
		return
	}

	if !helpers.CheckPasswordHash(context.PostForm("password"), user.Password) {
		context.JSON(http.StatusBadRequest, gin.H{"error": "incorrect password"})
		return
	}

	token, err := helpers.GenerateToken(user.ID)
	if err != nil {
		context.JSON(http.StatusInternalServerError, gin.H{"error": "error creating access token"})
		return
	}
	context.JSON(http.StatusOK, gin.H{"access_token": token})
	return
}
