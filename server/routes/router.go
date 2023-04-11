package routes

import (
	"server/middlewares"

	"github.com/gin-gonic/gin"
)

func NewRouter() *gin.Engine {
	router := gin.Default()

	authRoutes := router.Group("/")
	addAuthRoutes(authRoutes)

	authenticatedRoutes := router.Group("/")
	authenticatedRoutes.Use(middlewares.AuthMiddleware)
	addUserRoutes(authenticatedRoutes)

	clientRoutes := authenticatedRoutes.Group("/client")
	clientRoutes.Use(middlewares.AuthUserMiddleware)
	addClientRoutes(clientRoutes)

	adminRoutes := authenticatedRoutes.Group("/admin")
	adminRoutes.Use(middlewares.AuthAdminMiddleware)
	addAdminRoutes(adminRoutes)

	adminSignedRoutes := authenticatedRoutes.Group("/")
	adminSignedRoutes.Use(middlewares.AuthAdminMiddleware)
	adminSignedRoutes.Use(middlewares.VerifySignature)
	addAdminSignedRoutes(adminSignedRoutes)

	clientSignedRoutes := authenticatedRoutes.Group("/")
	clientSignedRoutes.Use(middlewares.AuthUserMiddleware)
	clientSignedRoutes.Use(middlewares.VerifySignature)
	addClientSignedRoutes(clientSignedRoutes)

	return router
}
