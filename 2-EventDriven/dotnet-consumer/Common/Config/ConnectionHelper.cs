using Microsoft.Extensions.Configuration;
using RabbitMQ.Client;

namespace CodeAcademy.DotnetConsumer.Common.Config;

public class ConnectionHelper
{
    private static readonly IConfiguration Configuration = new ConfigurationBuilder()
        .SetBasePath(AppContext.BaseDirectory)
        .AddJsonFile("appsettings.json", optional: true, reloadOnChange: false)
        .AddEnvironmentVariables()
        .Build();

    public static async Task<IConnection> ConnectAsync()
    {
        var rabbitMqUri = Configuration["RabbitMQ:URI"]
            ?? throw new InvalidOperationException("RabbitMQ:URI is not configured (set via appsettings.json or RabbitMQ__URI env var)");

        var factory = new ConnectionFactory { 
            Uri = new Uri(rabbitMqUri),
            Ssl = new SslOption
            {
                Enabled = false,
                ServerName = new Uri(rabbitMqUri).Host,
                CertificateValidationCallback = (sender, certificate, chain, sslPolicyErrors) => true // Accept all certificates (for demo purposes only)
            }
        };


        for (var i = 0; i < 5; i++)
        {
            try
            {
                return await factory.CreateConnectionAsync();
            }
            catch
            {
                if (i == 4) throw;
                Console.WriteLine($"Connection attempt {i + 1} failed, retrying in 2 seconds...");
                await Task.Delay(2000);
            }
        }

        throw new Exception("Failed to connect after 5 attempts");
    }
}
