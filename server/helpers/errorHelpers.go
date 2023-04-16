package helpers

import (
	"github.com/gin-gonic/gin"
	"net/http"
)

func SetStatusInternalServerError(context *gin.Context, msg string) {
	context.JSON(http.StatusInternalServerError, gin.H{"error": msg})
}

func SetStatusUnauthorized(context *gin.Context, msg string) {
	context.JSON(http.StatusUnauthorized, gin.H{"error": msg})
}

func SetStatusBadRequest(context *gin.Context, msg string) {
	context.JSON(http.StatusBadRequest, gin.H{"error": msg})
}
