import struct
import zlib
import os

def create_png(width, height, r, g, b, a=255):
    """Create a minimal valid PNG file with a solid color."""
    def make_chunk(chunk_type, data):
        chunk = chunk_type + data
        return struct.pack('>I', len(data)) + chunk + struct.pack('>I', zlib.crc32(chunk) & 0xffffffff)

    signature = b'\x89PNG\r\n\x1a\n'
    ihdr = struct.pack('>IIBBBBB', width, height, 8, 6, 0, 0, 0)

    raw = b''
    for y in range(height):
        raw += b'\x00'  # filter none
        for x in range(width):
            raw += bytes([r, g, b, a])

    idat = zlib.compress(raw)

    png = signature
    png += make_chunk(b'IHDR', ihdr)
    png += make_chunk(b'IDAT', idat)
    png += make_chunk(b'IEND', b'')
    return png

static_dir = r'E:\wechat\新能源充电管理平台用户端\static'
os.makedirs(static_dir, exist_ok=True)

icons = {
    'tab-home.png':           (153, 153, 153),
    'tab-home-active.png':    (74, 144, 226),
    'tab-order.png':          (153, 153, 153),
    'tab-order-active.png':   (74, 144, 226),
    'tab-profile.png':        (153, 153, 153),
    'tab-profile-active.png': (74, 144, 226),
    'default-avatar.png':     (200, 200, 210),
    'marker.png':             (74, 144, 226),
    'logo.png':               (102, 126, 234),
}

for name, (r, g, b) in icons.items():
    size = 81 if 'tab-' in name else (160 if name == 'logo.png' else 48 if name == 'marker.png' else 120)
    png = create_png(size, size, r, g, b)
    path = os.path.join(static_dir, name)
    with open(path, 'wb') as f:
        f.write(png)
    print(f'Created: {name} ({size}x{size}, {len(png)} bytes)')

print('Done!')
