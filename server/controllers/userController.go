package controllers

import (
	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"net/http"
	"server/db"
	"server/models"
)

func GetUser(context *gin.Context) {
	database := db.GetDatabase()
	usersCollection := database.Collection("users")

	var user models.User
	id, err := primitive.ObjectIDFromHex(context.GetString("user_id"))
	if err != nil {
		context.JSON(http.StatusInternalServerError, gin.H{"error": "invalid user id"})
		return
	}

	err = usersCollection.FindOne(context, bson.M{"_id": id}).Decode(&user)
	if err != nil {
		context.JSON(http.StatusInternalServerError, gin.H{"error": "user not found"})
		return
	}

	context.JSON(http.StatusOK, gin.H{
		"name":     user.Name,
		"email":    user.Email,
		"user_img": user.UserImg,
	})
	return
}
