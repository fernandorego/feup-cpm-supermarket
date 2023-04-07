package middlewares

import (
	"server/helpers"

	"strings"

	"github.com/gin-gonic/gin"
)

func AuthMiddleware(context *gin.Context) {
	claims, err := getClaims(context)
	if err {
		return
	}

	context.Set("user_id", claims.UserID)
	context.Next()
}

func AuthUserMiddleware(context *gin.Context) {
	claims, err := getClaims(context)
	if err {
		return
	}
	if claims.IsAdmin {
		helpers.SetStatusUnauthorized(context, "Permission denied for administrator account")
		context.Abort()
		return
	}
	context.Set("user_id", claims.UserID)
	context.Next()
}

func AuthAdminMiddleware(context *gin.Context) {
	claims, err := getClaims(context)
	if err {
		return
	}
	if !claims.IsAdmin {
		helpers.SetStatusUnauthorized(context, "Permission denied for non-administrator account")
		context.Abort()
		return
	}
	context.Next()
}

func getClaims(context *gin.Context) (*helpers.SignedDetails, bool) {
	authorization := strings.Split(context.Request.Header.Get("Authorization"), " ")
	if len(authorization) != 2 || authorization[0] != "Bearer" {
		helpers.SetStatusInternalServerError(context, "token not provided")
		context.Abort()
		return nil, true
	}
	claims, err := helpers.ValidateToken(authorization[1])
	if err != nil {
		helpers.SetStatusUnauthorized(context, err.Error())
		context.Abort()
		return nil, true
	}
	return claims, false
}
