package helpers

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/mongo"
)

func CheckUnique(context *gin.Context, collection *mongo.Collection, filter interface{}, errorMessage string) bool {
	count, err := collection.CountDocuments(context, filter)
	if err != nil {
		context.JSON(http.StatusBadRequest, gin.H{"error": "error occured while counting documents"})
		return false
	}
	if count > 0 {
		context.JSON(http.StatusInternalServerError, gin.H{"error": errorMessage})
		return false
	}
	return true
}
