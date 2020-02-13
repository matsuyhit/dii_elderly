# coding=utf-8
import socket
import concurrent.futures

class TcpServer:
    def __init__(self, address, port, recv_size=1024):

        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.bind((address, port))  
        self.socket.listen(5)

        print('クライアントデバイスからの接続待ち....')
        self.client_socket, self.client_info = self.socket.accept()
        print("接続完了")

        self.recv_func = lambda: self.client_socket.recv(recv_size)
        self.send_func = lambda data: self.client_socket.sendall(data)

        self.executor = concurrent.futures.ThreadPoolExecutor(max_workers=1)

    def recv_str(self):
        future = self.executor.submit(self.recv_func)
        result = future.result()
        # 受け取ったデータをutf8にデコードする
        return bytes(result).decode('utf-8')

    def send_str(self, data):
        self.executor.submit(self.send_func, bytes(data.encode('utf-8')))
        print(bytes(self.executor.submit(self.send_func, bytes(data.encode('utf-8'))).result()).decode('utf-8'))

