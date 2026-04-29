using CodeAcademy.DotnetConsumer.Infrastructure.Models;
using Microsoft.EntityFrameworkCore;

namespace CodeAcademy.DotnetConsumer.Infrastructure.Repository;

public class IdemRepository
{
    private readonly IdemDbContext _db;

    public IdemRepository(IdemDbContext db)
    {
        _db = db;
    }

    public async Task AddAsync(Idem idem, CancellationToken cancellationToken = default)
    {
        await _db.Idems.AddAsync(idem, cancellationToken);
        await _db.SaveChangesAsync(cancellationToken);
    }

    public Task<List<Idem>> FindLatestAsync(int limit = 50, CancellationToken cancellationToken = default)
    {
        return _db.Idems
            .OrderByDescending(i => i.CreatedAt)
            .Take(limit)
            .ToListAsync(cancellationToken);
    }

    public Task<Idem?> FindByIdAsync(Guid id, CancellationToken cancellationToken = default)
    {
        return _db.Idems.FirstOrDefaultAsync(i => i.Id == id, cancellationToken);
    }
}
