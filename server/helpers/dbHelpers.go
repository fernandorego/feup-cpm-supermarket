package helpers

import (
	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/mongo"
)

func IsUnique(context *gin.Context, collection *mongo.Collection, filter interface{}, errorMessage string) bool {
	count, err := collection.CountDocuments(context, filter)
	if err != nil {
		SetStatusBadRequest(context, "error occured while counting documents")
		return false
	}
	if count > 0 {
		SetStatusInternalServerError(context, errorMessage)
		return false
	}
	return true
}
