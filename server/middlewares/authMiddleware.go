package middlewares

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"server/helpers"
)

func AuthMiddleware(context *gin.Context) {
	token := context.Request.Header.Get("access_token")
	if token == "" {
		context.JSON(http.StatusInternalServerError, gin.H{"error": "token not provided"})
		context.Abort()
		return
	}
	claims, err := helpers.ValidateToken(token)
	if err != "" {
		context.JSON(http.StatusBadRequest, gin.H{"error": err})
		context.Abort()
		return
	}
	context.Set("user_id", claims.UserID)
	context.Next()
}
