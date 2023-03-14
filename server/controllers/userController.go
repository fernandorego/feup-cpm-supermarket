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

func GetUser(context *gin.Context) {
	database := db.GetDatabase()
	usersCollection := database.Collection("users")

	id, exists := context.Get("user_id")
	if !exists {
		helpers.SetStatusInternalServerError(context, "user id not provided")
		return
	}

	var user models.User
	if err := usersCollection.FindOne(context, bson.M{"_id": id.(primitive.ObjectID)}).Decode(&user); err != nil {
		helpers.SetStatusInternalServerError(context, "user not found")
		return
	}

	context.JSON(http.StatusOK, gin.H{
		"name":     user.Name,
		"email":    user.Email,
		"user_img": user.UserImg,
	})
	return
}
