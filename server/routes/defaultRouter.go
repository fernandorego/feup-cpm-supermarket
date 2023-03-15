package routes

import (
	"github.com/gin-gonic/gin"
	"net/http"
)

func addDefaultRoutes(rg *gin.RouterGroup) {
	rg.GET("/ping", func(c *gin.Context) {
		c.String(http.StatusOK, "pong")
	})
}
