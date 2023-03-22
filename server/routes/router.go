package routes

import (
	"github.com/gin-gonic/gin"
	"server/middlewares"
)

func NewRouter() *gin.Engine {
	router := gin.Default()

	defaultRoutes := router.Group("default/")
	addDefaultRoutes(defaultRoutes)

	authRoutes := router.Group("/")
	addAuthRoutes(authRoutes)

	getUserRoute := router.Group("/getUser")
	getUserRoute.Use(middlewares.AuthMiddleware())
	addGetTokenRoute(getUserRoute)

	userRoutes := router.Group("/user")
	userRoutes.Use(middlewares.AuthUserMiddleware)
	addUserRoutes(userRoutes)

	adminRoutes := router.Group("/admin")
	adminRoutes.Use(middlewares.AuthAdminMiddleware)
	addAdminRoutes(adminRoutes)

	return router
}
