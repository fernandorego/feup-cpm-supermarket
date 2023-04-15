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

	adminSelfSignedRoutes := authenticatedRoutes.Group("/")
	adminSelfSignedRoutes.Use(middlewares.AuthAdminMiddleware)
	adminSelfSignedRoutes.Use(middlewares.VerifyOwnSignature)
	addAdminSelfSignedRoutes(adminSelfSignedRoutes)

	adminOthersSignedRoutes := authenticatedRoutes.Group("/")
	adminOthersSignedRoutes.Use(middlewares.AuthAdminMiddleware)
	adminOthersSignedRoutes.Use(middlewares.VerifyOthersSignature)
	addAdminOthersSignedRoutes(adminOthersSignedRoutes)

	clientSignedRoutes := authenticatedRoutes.Group("/")
	clientSignedRoutes.Use(middlewares.AuthUserMiddleware)
	clientSignedRoutes.Use(middlewares.VerifyOwnSignature)
	addClientSignedRoutes(clientSignedRoutes)

	return router
}
