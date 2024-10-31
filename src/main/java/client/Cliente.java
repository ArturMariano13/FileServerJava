package client;

import java.io.*;
import java.net.*;

public class Cliente {

    public static void main(String[] args) {
        String enderecoServidor = "localhost";
        int porta = 1234;

        try {
            // Estabelecer a conexão com o ServerSocket
            Socket socket = new Socket(enderecoServidor, porta);
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Exemplo de login, se necessário
            String login = "LOGIN;aluno;computacao";
            saida.println(login);
            String loginRes = entrada.readLine();
            System.out.println("Resposta do Login: " + loginRes);

            // Operação: Criar um arquivo
            String requisicao = "CREATE;meuarquivo.txt;Este é o conteúdo do arquivo";
            saida.println(requisicao);
            String resposta = entrada.readLine();
            System.out.println("Resposta do servidor: " + resposta);

            // Operação: Ler o conteúdo de um arquivo
            requisicao = "READ;meuarquivo.txt";
            saida.println(requisicao);
            resposta = entrada.readLine();
            System.out.println("Conteúdo do arquivo: " + resposta);

            // Operação: Atualizar o conteúdo de um arquivo
            requisicao = "UPDATE;meuarquivo.txt;Novo conteúdo atualizado";
            saida.println(requisicao);
            resposta = entrada.readLine();
            System.out.println("Resposta do servidor: " + resposta);

            // Operação: Deletar um arquivo
            requisicao = "DELETE;meuarquivo.txt";
            saida.println(requisicao);
            resposta = entrada.readLine();
            System.out.println("Resposta do servidor: " + resposta);

            // Fazer logout
            requisicao = "LOGOUT";
            saida.println(requisicao);
            resposta = entrada.readLine();
            System.out.println("Resposta Logout: " + resposta);

            // Sair
            requisicao = "SAIR";
            saida.println(requisicao);
            resposta = entrada.readLine();
            System.out.println("Resposta SAIR: " + resposta);

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
