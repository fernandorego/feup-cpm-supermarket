package routes

import (
	"server/middlewares"

	"github.com/gin-gonic/gin"
)

func NewRouter() *gin.Engine {
	router := gin.Default()

	authRoutes := router.Group("/")
	addAuthRoutes(authRoutes)

	userRoutes := router.Group("/")
	userRoutes.Use(middlewares.AuthMiddleware)
	addUserRoutes(userRoutes)

	clientRoutes := router.Group("/client")
	clientRoutes.Use(middlewares.AuthUserMiddleware)
	addClientRoutes(clientRoutes)

	adminRoutes := router.Group("/admin")
	adminRoutes.Use(middlewares.AuthAdminMiddleware)
	addAdminRoutes(adminRoutes)

	adminSignedRoutes := router.Group("/")
	adminSignedRoutes.Use(middlewares.AuthAdminMiddleware)
	addAdminSecuredRoutes(adminSignedRoutes)

	return router
}
