package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteArquivos {

    private Socket socket;
    private PrintWriter saida;
    private BufferedReader entrada;

    public ClienteArquivos(String endereco, int porta) throws IOException {
        socket = new Socket(endereco, porta);
        saida = new PrintWriter(socket.getOutputStream(), true);
        entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String enviarComando(String comando) throws IOException {
        // Envia o comando ao servidor
        saida.println(comando);
        // Lê a resposta do servidor
        return entrada.readLine();
    }

    public void fecharConexao() throws IOException {
        entrada.close();
        saida.close();
        socket.close();
    }

    public static void main(String[] args) {
        try {
            ClienteArquivos cliente = new ClienteArquivos("localhost", 1234);
            Scanner scanner = new Scanner(System.in);
            boolean conectado = true;

            System.out.println("Bem-vindo ao Cliente de Arquivos");

            while (conectado) {
                System.out.print("Digite um comando (LOGIN, DOW, UPL, LST, LOGOUT, SAIR): ");
                String comando = scanner.nextLine();
                String resposta;

                switch (comando.toUpperCase()) {
                    case "LOGIN":
                        System.out.print("Digite o usuário: ");
                        String usuario = scanner.nextLine();
                        System.out.print("Digite a senha: ");
                        String senha = scanner.nextLine();
                        resposta = cliente.enviarComando("LOGIN;" + usuario + ";" + senha);
                        System.out.println("Resposta do servidor: " + resposta);
                        break;

                    case "DOW":
                        System.out.print("Digite o nome do arquivo para download: ");
                        String nomeArquivoDownload = scanner.nextLine();
                        resposta = cliente.enviarComando("DOW;" + nomeArquivoDownload);
                        System.out.println("Resposta do servidor: " + resposta);
                        break;

                    case "UPL":
                        System.out.print("Digite o nome do arquivo para upload: ");
                        String nomeArquivoUpload = scanner.nextLine();
                        System.out.print("Digite o conteúdo do arquivo: ");
                        String conteudoArquivo = scanner.nextLine();
                        resposta = cliente.enviarComando("UPL;" + nomeArquivoUpload + ";" + conteudoArquivo);
                        System.out.println("Resposta do servidor: " + resposta);
                        break;

                    case "LST":
                        resposta = cliente.enviarComando("LST");
                        System.out.println("Resposta do servidor: " + resposta);
                        break;

                    case "LOGOUT":
                        resposta = cliente.enviarComando("LOGOUT");
                        System.out.println("Resposta do servidor: " + resposta);
                        break;

                    case "SAIR":
                        resposta = cliente.enviarComando("SAIR");
                        System.out.println("Resposta do servidor: " + resposta);
                        conectado = false; // Termina o loop para desconectar o cliente
                        break;

                    default:
                        System.out.println("Comando não reconhecido.");
                }
            }

            cliente.fecharConexao();
            System.out.println("Conexão encerrada.");

        } catch (IOException e) {
            System.out.println("Erro na comunicação com o servidor: " + e.getMessage());
        }
    }
}
