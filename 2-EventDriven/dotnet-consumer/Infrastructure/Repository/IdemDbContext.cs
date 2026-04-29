using CodeAcademy.DotnetConsumer.Infrastructure.Models;
using Microsoft.EntityFrameworkCore;

namespace CodeAcademy.DotnetConsumer.Infrastructure.Repository;

public class IdemDbContext : DbContext
{
    public DbSet<Idem> Idems => Set<Idem>();

    public IdemDbContext()
    {
    }

    public IdemDbContext(DbContextOptions<IdemDbContext> options) : base(options)
    {
    }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        if (!optionsBuilder.IsConfigured)
        {
            optionsBuilder.UseNpgsql(BuildConnectionString());
        }
    }

    private static string BuildConnectionString()
    {
        // Full override wins if provided
        var full = Environment.GetEnvironmentVariable("POSTGRES_CONNECTION_STRING");
        if (!string.IsNullOrWhiteSpace(full))
        {
            return full;
        }

        // Otherwise compose from the same env vars used by docker-compose.yml
        var host = Environment.GetEnvironmentVariable("POSTGRES_HOST") ?? "localhost";
        var port = Environment.GetEnvironmentVariable("POSTGRES_PORT") ?? "5435";
        var db = Environment.GetEnvironmentVariable("POSTGRES_DB") ?? "codeacademy";
        var user = Environment.GetEnvironmentVariable("POSTGRES_USER") ?? "codeacademy";
        var password = Environment.GetEnvironmentVariable("POSTGRES_PASSWORD") ?? "codeacademy";

        return $"Host={host};Port={port};Database={db};Username={user};Password={password}";
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Idem>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.HasIndex(e => e.CreatedAt).IsDescending();
        });
    }
}
