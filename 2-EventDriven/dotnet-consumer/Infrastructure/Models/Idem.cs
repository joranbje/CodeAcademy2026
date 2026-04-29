using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace CodeAcademy.DotnetConsumer.Infrastructure.Models;

[Table("idems")]
public class Idem
{
    [Key]
    [Column("id")]
    public Guid Id { get; set; }

    [Required]
    [Column("author")]
    public string Author { get; set; } = string.Empty;

    [Required]
    [Column("message")]
    public string Message { get; set; } = string.Empty;

    [Column("created_at")]
    public DateTime CreatedAt { get; set; }
}
