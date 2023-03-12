package db

import (
	"context"
	"fmt"
	"os"

	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
	"go.mongodb.org/mongo-driver/mongo/readpref"
)

var database *mongo.Database

func InitDB() *mongo.Client {
	ctx := context.TODO()
	// Use the SetServerAPIOptions() method to set the Stable API version to 1
	stableServerApi := options.ServerAPI(options.ServerAPIVersion1)
	clientOptions := options.Client().ApplyURI(os.Getenv("DATABASE_URI")).SetServerAPIOptions(stableServerApi)

	client, err := mongo.Connect(ctx, clientOptions)
	if err != nil {
		panic(err)
	}

	err = client.Ping(ctx, readpref.Primary())
	if err != nil {
		panic(err)
	}
	fmt.Println("Pinged your deployment. You successfully connected to MongoDB!")

	database = client.Database("appDB")
	return client
}

func DisconnectDB(client *mongo.Client) {
	if err := client.Disconnect(context.TODO()); err != nil {
		panic(err)
	}
}

func GetDatabase() *mongo.Database {
	return database
}
