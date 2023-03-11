package db

import (
	"context"
	"fmt"

	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
	"go.mongodb.org/mongo-driver/mongo/readpref"
)

const uri = "mongodb://admin:password@localhost:27017"

var client *mongo.Client

func InitDB() {
	ctx := context.TODO()
	// Use the SetServerAPIOptions() method to set the Stable API version to 1
	stableServerApi := options.ServerAPI(options.ServerAPIVersion1)
	clientOptions := options.Client().ApplyURI(uri).SetServerAPIOptions(stableServerApi)

	clt, err := mongo.Connect(ctx, clientOptions)
	if err != nil {
		panic(err)
	}

	err = clt.Ping(ctx, readpref.Primary())
	if err != nil {
		panic(err)
	}
	fmt.Println("Pinged your deployment. You successfully connected to MongoDB!")
	client = clt
}

func DisconnectDB() {
	if err := client.Disconnect(context.TODO()); err != nil {
		panic(err)
	}
}

func GetDBClient() *mongo.Client {
	return client
}
