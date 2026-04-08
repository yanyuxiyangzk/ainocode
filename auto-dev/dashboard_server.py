#!/usr/bin/env python3
"""
Dashboard Server - HTTP服务展示Pipeline仪表盘

用法:
  python dashboard_server.py              # 默认 8080 端口
  python dashboard_server.py --port 9000 # 指定端口
  python dashboard_server.py --open       # 自动打开浏览器
"""

import argparse
import webbrowser
import threading
from http.server import HTTPServer, SimpleHTTPRequestHandler
from pathlib import Path

AUTO_DEV_BASE = Path(__file__).parent
PORT = 8080


class DashboardHandler(SimpleHTTPRequestHandler):
    """自定义HTTP处理器"""

    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=str(AUTO_DEV_BASE), **kwargs)

    def do_GET(self):
        """处理GET请求"""
        if self.path == '/' or self.path == '/dashboard.html':
            self.path = '/dashboard.html'
        elif self.path.startswith('/tasks/pipeline/'):
            # 代理访问 status.json
            pass
        return super().do_GET()

    def log_message(self, format, *args):
        """自定义日志格式"""
        print(f"[DASHBOARD] {args[0]}")


def get_local_ip():
    """获取本机局域网IP"""
    import socket
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
        s.close()
        return ip
    except:
        return "127.0.0.1"


def main():
    global PORT

    parser = argparse.ArgumentParser(description="Dashboard HTTP Server")
    parser.add_argument("--port", "-p", type=int, default=8080, help="端口号 (默认 8080)")
    parser.add_argument("--open", "-o", action="store_true", help="自动打开浏览器")
    args = parser.parse_args()

    PORT = args.port

    # 获取本机IP
    local_ip = get_local_ip()

    # 启动服务器
    server = HTTPServer(('0.0.0.0', PORT), DashboardHandler)

    print(f"""
╔══════════════════════════════════════════════════════╗
║           Auto-Dev 2.0 Dashboard Server              ║
╠══════════════════════════════════════════════════════╣
║  Local:   http://localhost:{PORT}/dashboard.html        ║
║  Network:  http://{local_ip}:{PORT}/dashboard.html    ║
╠══════════════════════════════════════════════════════╣
║  Press Ctrl+C to stop server                        ║
╚══════════════════════════════════════════════════════╝
""")

    # 自动打开浏览器
    if args.open:
        threading.Timer(1.0, lambda: webbrowser.open(f"http://localhost:{PORT}/dashboard.html")).start()

    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\n[DASHBOARD] Server stopped")
        server.shutdown()


if __name__ == "__main__":
    main()
