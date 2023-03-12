package controllers

import (
	"github.com/gin-gonic/gin"
	"net/http"
)

func Ping(context *gin.Context) {
	context.String(http.StatusOK, "pong")
	return
}
