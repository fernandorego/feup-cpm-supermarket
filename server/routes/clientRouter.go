package routes

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

func addClientRoutes(rg *gin.RouterGroup) {
	rg.GET("/ping", func(c *gin.Context) {
		c.String(http.StatusOK, "pong")
	})
}
