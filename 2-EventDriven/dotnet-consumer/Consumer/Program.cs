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



// Create a channel and declare the queue
using var channel = await connection.CreateChannelAsync();
await channel.QueueDeclareAsync(queue: "idem-events", durable: true, exclusive: false, autoDelete: false, arguments: null);

// Set up a consumer to listen for messages
var consumer = new AsyncEventingBasicConsumer(channel);

// Handle received messages
consumer.ReceivedAsync += async (sender, eventArgs) =>
{
    var body = eventArgs.Body.ToArray();
    var message = JsonSerializer.Deserialize<JsonNode>(Encoding.UTF8.GetString(body));

    Console.WriteLine($"Received message: {message}");

    // Simulate processing time
    await Task.Delay(1000);

    // Acknowledge the message
    await channel.BasicAckAsync(eventArgs.DeliveryTag, multiple: false);   
};
// Start consuming messages
await channel.BasicConsumeAsync(queue: "idem-events", autoAck: false, consumerTag: "", noLocal: false, exclusive: false, arguments: null, consumer: consumer);
Console.ReadLine(); // Keep the application running to listen for messages