using System.Text;
using System.Text.Json;
using System.Text.Json.Nodes;
using CodeAcademy.DotnetConsumer.Common.Config;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;

Console.WriteLine("Starting Consumer application...");

// Establish connection to RabbitMQ
using var connection = await ConnectionHelper.ConnectAsync();
Console.WriteLine("Connected to RabbitMQ");

// Implement a basic consumer here.
// Start with:
// - Create a channel
// - Declare a queue
// - Create a consumer and subscribe to the queue
// - Handle incoming messages by deserializing the JSON and printing the content to the console