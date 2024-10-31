package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorArquivos {

    int porta;
    ServerSocket servidorSocket;
    Map<String, Arquivo> arquivos; // Armazena os arquivos enviados pelos clientes

    public ServidorArquivos(int porta) {
        this.porta = porta;
        this.arquivos = new HashMap<>();
    }

    public void criaServerSocket() throws IOException {
        servidorSocket = new ServerSocket(porta);
    }

    public Socket esperaConexao() throws IOException {
        System.out.println("Esperando conexão...");
        return servidorSocket.accept();
    }

    public void trataProtocolo(Socket socket) throws IOException {
    try {
        BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);

        Estado estado = Estado.CONECTADO;

        while (estado != Estado.FINALIZADO) {
            String mensagem = entrada.readLine();

            String[] protocolo = mensagem.split(";");
            String operacao = protocolo[0];
            String resposta = "";

            switch (estado) {
                case CONECTADO:
                    switch (operacao) {
                        case "LOGIN":
                            String user = protocolo[1];
                            String pass = protocolo[2];
                            if (user.equals("aluno") && pass.equals("computacao")) {
                                resposta = "LOGINRESPONSE;OK";
                                estado = Estado.AUTENTICADO;
                            } else {
                                resposta = "LOGINRESPONSE;ERRO;Credenciais inválidas";
                            }
                            break;
                        case "SAIR":
                            resposta = "SAIRRESPONSE;OK";
                            estado = Estado.FINALIZADO;
                            break;
                        default:
                            resposta = operacao + "RESPONSE;ERRO;Operação não permitida no estado atual";
                    }
                    break;

                case AUTENTICADO:
                    switch (operacao) {
                        case "LOGOUT":
                            resposta = "LOGOUTRESPONSE;OK";
                            estado = Estado.CONECTADO;
                            break;

                        case "CREATE":
                            if (protocolo.length > 2) {
                                String fileName = protocolo[1];
                                String content = protocolo[2];
                                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                                    writer.write(content);
                                    resposta = "CREATERESPONSE;OK";
                                } catch (IOException e) {
                                    resposta = "CREATERESPONSE;ERRO;Falha ao criar o arquivo";
                                }
                            } else {
                                resposta = "CREATERESPONSE;ERRO;Argumentos insuficientes";
                            }
                            break;

                        case "READ":
                            if (protocolo.length > 1) {
                                String fileName = protocolo[1];
                                StringBuilder content = new StringBuilder();
                                try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        content.append(line).append("\n");
                                    }
                                    resposta = "READRESPONSE;OK;" + content.toString().trim();
                                } catch (IOException e) {
                                    resposta = "READRESPONSE;ERRO;Arquivo não encontrado";
                                }
                            } else {
                                resposta = "READRESPONSE;ERRO;Argumentos insuficientes";
                            }
                            break;

                        case "UPDATE":
                            if (protocolo.length > 2) {
                                String fileName = protocolo[1];
                                String newContent = protocolo[2];
                                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
                                    writer.write(newContent);
                                    resposta = "UPDATERESPONSE;OK";
                                } catch (IOException e) {
                                    resposta = "UPDATERESPONSE;ERRO;Falha ao atualizar o arquivo";
                                }
                            } else {
                                resposta = "UPDATERESPONSE;ERRO;Argumentos insuficientes";
                            }
                            break;

                        case "DELETE":
                            if (protocolo.length > 1) {
                                String fileName = protocolo[1];
                                File file = new File(fileName);
                                if (file.delete()) {
                                    resposta = "DELETERESPONSE;OK";
                                } else {
                                    resposta = "DELETERESPONSE;ERRO;Falha ao deletar o arquivo";
                                }
                            } else {
                                resposta = "DELETERESPONSE;ERRO;Argumentos insuficientes";
                            }
                            break;

                        default:
                            resposta = operacao + "RESPONSE;ERRO;Operação não reconhecida";
                    }
                    break;
            }

            saida.println(resposta);
        }
    } catch (Exception e) {
        System.out.println("Erro nos streams " + e);
    } finally {
        socket.close();
    }
}


    public static void main(String[] args) {
        try {
            ServidorArquivos server = new ServidorArquivos(1234);
            server.criaServerSocket();
            while (true) {
                Socket socket = server.esperaConexao();
                server.trataProtocolo(socket);
            }
        } catch (IOException e) {
            System.out.println("Erro ao processar a conexão do cliente: " + e.getMessage());
        }
    }
}