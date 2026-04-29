# .NET Event-Driven Workshop

A multi-project .NET solution demonstrating event-driven architecture with RabbitMQ. Includes a `Producer` that publishes events, a `Consumer` that reads them, and an `Infrastructure` project for optional PostgreSQL persistence via EF Core.

See [oppgaver.md](oppgaver.md) for workshop tasks.

## Prerequisites

- .NET 10 SDK
- RabbitMQ running on localhost:5672 (see the repo-root `docker-compose.yml`)
- PostgreSQL running on localhost:5435 (optional, only for the Infrastructure exercises — see `docker-compose.yml` in this folder)
- Podman or docker installed

## Project Structure

```
dotnet-consumer/
├── DotnetEvents.slnx               # Solution file (open in VS Code / Visual Studio)
├── Dockerfile                      # Docker build (to be expanded in workshop)
├── appsettings.json                # RabbitMQ connection configuration
├── Common/                         # Shared library
│   ├── Common.csproj
│   └── Config/
│       ├── ConnectionHelper.cs     # Builds IConnection from appsettings.json
│       └── QueueConfig.cs          # Optional multi-queue configuration
├── Consumer/                       # Executable: reads messages from RabbitMQ
│   ├── Consumer.csproj
│   └── Program.cs
├── Producer/                       # Executable: publishes messages to RabbitMQ
│   ├── Producer.csproj
│   └── Program.cs
└── Infrastructure/                 # Optional: EF Core + PostgreSQL persistence
    ├── Infrastructure.csproj
    ├── Models/
    │   └── Idem.cs                 # EF Core entity mapped to the `idems` table
    ├── Repository/
    │   ├── IdemDbContext.cs        # EF Core DbContext (Npgsql)
    │   └── IdemRepository.cs       # Add / query idems
    └── Migrations/                 # EF Core migrations (auto-generated)
```

## Quick Start

From the `dotnet-consumer/` folder:

```bash
# 1. Start RabbitMQ (and optionally PostgreSQL) in the background
#    Docker:
docker compose up -d
#    Podman (equivalent):
podman compose up -d
#    …or with podman-compose:
podman-compose up -d

# 2. Restore and build everything
dotnet build DotnetEvents.slnx

# 3. Run the Producer (publishes idem events every 2 seconds)
dotnet run --project Producer

# 4. In a separate terminal, run the Consumer
dotnet run --project Consumer

# When you're done
docker compose down        # or: podman compose down
```

RabbitMQ management UI: http://localhost:15672 (guest/guest).
PostgreSQL: `localhost:5435` (user/db/password all `codeacademy`).

## Configuration

Connection details are loaded from `appsettings.json` by `ConnectionHelper`. Environment variables override JSON (use `__` for nested keys, e.g. `RabbitMQ__URI`).

```json
{
  "RabbitMQ": {
    "URI": "amqp://guest:guest@localhost:5672/"
  },
  "RabbitMQRemote": {
    "URI": "amqps://user:password@host:5671/"
  }
}
```

| Key | Description |
|---|---|
| `RabbitMQ:URI` | AMQP connection string for the local RabbitMQ |
| `RabbitMQRemote:URI` | Optional remote/shared RabbitMQ (use `amqps://` and port 5671 for TLS) |

`appsettings.json` sits at the solution root and is copied to each executable's output folder on build.

## Database (optional — Infrastructure project)

The `Infrastructure` project provides EF Core + Npgsql persistence for storing idem events in PostgreSQL. Migrations are applied automatically when `db.Database.Migrate()` is called on an `IdemDbContext`.

The `Idem` entity mirrors the shape used by the Java consumer (`IdemDataDTO` / `MessageData`):

| Column | Type | Notes |
|---|---|---|
| `id` | `uuid` | Primary key |
| `author` | `text` | Required |
| `message` | `text` | Required |
| `created_at` | `timestamp with time zone` | Descending index |

### Using the repository from Producer / Consumer

By default, only `Infrastructure` references EF Core. To persist idems from the `Producer` or `Consumer`, add a project reference to `Infrastructure`:

```xml
<!-- Consumer/Consumer.csproj or Producer/Producer.csproj -->
<ItemGroup>
  <ProjectReference Include="../Common/Common.csproj" />
  <ProjectReference Include="../Infrastructure/Infrastructure.csproj" />
</ItemGroup>
```

Then open a `IdemDbContext`, apply migrations once at startup, and use `IdemRepository` to read/write. A typical consumer callback looks like this:

```csharp
using CodeAcademy.DotnetConsumer.Infrastructure.Models;
using CodeAcademy.DotnetConsumer.Infrastructure.Repository;

// Once at startup
using var db = new IdemDbContext();
db.Database.Migrate();
var idems = new IdemRepository(db);

// Per received message (in your BasicConsumeAsync handler)
var idem = new Idem
{
    Id = Guid.NewGuid(),
    Author = payload.Author,
    Message = payload.Message,
    CreatedAt = DateTime.UtcNow
};
await idems.AddAsync(idem);

// Query the 50 most recent
var latest = await idems.FindLatestAsync();
```

The same pattern works from `Producer` if you want to log what was published.

### Managing migrations

```bash
# Install the tool (once)
dotnet tool install --global dotnet-ef

# Add a new migration after modifying the Idem entity or DbContext
dotnet ef migrations add <MigrationName> --project Infrastructure --context IdemDbContext

# Apply migrations to the database
dotnet ef database update --project Infrastructure --context IdemDbContext

# Remove the last migration (if not applied yet)
dotnet ef migrations remove --project Infrastructure --context IdemDbContext
```

The connection string defaults to `Host=localhost;Port=5435;Database=codeacademy;Username=codeacademy;Password=codeacademy` (matching `docker-compose.yml`), overridable via the `POSTGRES_CONNECTION_STRING` environment variable.

## Dependencies

- RabbitMQ.Client 7.1.2
- Microsoft.Extensions.Configuration (JSON + EnvironmentVariables)
- Microsoft.EntityFrameworkCore 10.0.0 *(Infrastructure only)*
- Npgsql.EntityFrameworkCore.PostgreSQL 10.0.0 *(Infrastructure only)*
