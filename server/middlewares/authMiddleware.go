package middlewares

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"server/helpers"
	"strings"
)

func AuthMiddleware(context *gin.Context) {
	authorization := strings.Split(context.Request.Header.Get("Authorization"), " ")
	if len(authorization) != 2 || authorization[0] != "Bearer" {
		context.JSON(http.StatusInternalServerError, gin.H{"error": "token not provided"})
		context.Abort()
		return
	}
	claims, err := helpers.ValidateToken(authorization[1])
	if err != nil {
		context.JSON(http.StatusUnauthorized, gin.H{"error": err.Error()})
		context.Abort()
		return
	}
	context.Set("user_id", claims.UserID)
	context.Next()
}
