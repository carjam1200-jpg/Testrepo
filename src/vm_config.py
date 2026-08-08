"""VM configuration model for vmtest."""
from dataclasses import dataclass, asdict

@dataclass
class VMConfig:
    name: str
    ram_mb: int = 2048
    cpus: int = 2
    iso: str = ''
    disk: str = ''

    def to_dict(self):
        return asdict(self)

    @classmethod
    def from_dict(cls, data):
        return cls(
            name=data.get('name', 'VM'),
            ram_mb=int(data.get('ram_mb', data.get('ram', 2048))),
            cpus=int(data.get('cpus', 2)),
            iso=data.get('iso', ''),
            disk=data.get('disk', ''),
        )
