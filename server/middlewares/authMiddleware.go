package middlewares

import (
	"github.com/gin-gonic/gin"
	"server/helpers"
	"strings"
)

func AuthMiddleware(context *gin.Context) {
	authorization := strings.Split(context.Request.Header.Get("Authorization"), " ")
	if len(authorization) != 2 || authorization[0] != "Bearer" {
		helpers.SetStatusInternalServerError(context, "token not provided")
		context.Abort()
		return
	}
	claims, err := helpers.ValidateToken(authorization[1])
	if err != nil {
		helpers.SetStatusUnauthorized(context, err.Error())
		context.Abort()
		return
	}
	context.Set("user_id", claims.UserID)
	context.Next()
}
