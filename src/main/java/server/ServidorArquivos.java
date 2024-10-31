package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*        1 - Criar o servidor de conexÃµes
 	* 2 -Esperar o um pedido de conexÃ£o; // Outro processo 2.1 e criar uma
 	* nova conexÃ£o; 3 - Criar streams de enechar socket de comunicaÃ§Ã£o entre
 	* servidor/cliente 4.2 - Fechar streams de entrada e saÃ­da trada e saÃ­da;
 	* 4 - Tratar a conversaÃ§Ã£o entre cliente e servidor (tratar protocolo);
 	* 4.1 - Fechar socket de comunicaÃ§Ã£o entre servidor/cliente 4.2 - Fechar
 */
public class ServidorArquivos {

    int porta;
    ServerSocket servidorSocket;
    ArrayList<Arquivo> arquivos;
    

    public ServidorArquivos(int porta) {
        this.porta = porta;
    }
    /**
     * Faz upload de arquivo;
     * presume que os parametros ja estejam validados
     * @param nome
     * @param conteudo
     * @param dono
     * @return 
     */
    public StatusCode upload(String nome, String conteudo, String dono){
        
        //verificar se o arquivo já existe
        //criar arquivo
        //salvar arquivo
        
        return StatusCode.OK;
        
    }
    
    public Map<StatusCode, Arquivo> download(String nome){
        //busca o arquivo
        //verifica
        //envia StatusCode com conteudo;
        HashMap<StatusCode, Arquivo> map= new HashMap<>();
        map.put(StatusCode.OK, new Arquivo("ex", "nome", "conteudo"));
        
        return map;
    }
    
    public ArrayList<Arquivo> listarArquivos(String nome){
        //busca arquivos que contenham o nome
        //retorna array
        ArrayList<Arquivo> found = new ArrayList<>();
        
        return found;
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
                                resposta = "LOGINRESPONSE;ERROR;Credenciais inválidas";
                            }
                            break;
                        case "SAIR":
                            resposta = "SAIRRESPONSE;OK";
                            estado = Estado.FINALIZADO;
                            break;
                        default:
                            resposta = operacao + "RESPONSE;ERRO;MENSAGEM INVALIDA OU NÃO PERMITIDA";
                    }
                    break;
                case AUTENTICADO:
                    switch (operacao) {
                        case "DOW":
                            String nomeArquivo = protocolo[1];
                            Map<StatusCode, Arquivo> resultadoDownload = download(nomeArquivo);
                            if (resultadoDownload.containsKey(StatusCode.OK)) {
                                Arquivo arquivo = resultadoDownload.get(StatusCode.OK);
                                resposta = "DOWRESPONSE;OK;" + arquivo.getConteudo();
                            } else {
                                resposta = "DOWRESPONSE;NOTFOUND";
                            }
                            break;
                        case "UPL":
                            if (protocolo.length < 3) {
                                resposta = "UPLRESPONSE;PARAMERROR";
                            } else {
                                String nome = protocolo[1];
                                String conteudo = protocolo[2];
                                StatusCode statusUpload = upload(nome, conteudo, "aluno");
                                resposta = "UPLRESPONSE;" + statusUpload;
                            }
                            break;
                        case "LST":
                            ArrayList<Arquivo> listaArquivos = listarArquivos("");
                            if (!listaArquivos.isEmpty()) {
                                StringBuilder nomesArquivos = new StringBuilder();
                                for (Arquivo arquivo : listaArquivos) {
                                    nomesArquivos.append(arquivo.getNome()).append(",");
                                }
                                // Remove última vírgula
                                if (nomesArquivos.length() > 0) {
                                    nomesArquivos.setLength(nomesArquivos.length() - 1);
                                }
                                resposta = "LSTRESPONSE;OK;" + nomesArquivos;
                            } else {
                                resposta = "LSTRESPONSE;NOTFOUND";
                            }
                            break;
                        case "LOGOUT":
                            resposta = "LOGOUTRESPONSE;OK";
                            estado = Estado.CONECTADO;
                            break;
                        case "SAIR":
                            resposta = "SAIRRESPONSE;OK";
                            estado = Estado.FINALIZADO;
                            break;
                        default:
                            resposta = operacao + "RESPONSE;ERRO;Mensagem inválida ou não autorizada";
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

        try { //1.criando o ServerSocket. Servidor de conexão TCP
            ServidorArquivos server = new ServidorArquivos(1234);
            server.criaServerSocket();
            //loop de conexões
            while (true) { //n pode ser infinito
                Socket socket = server.esperaConexao();
                server.trataProtocolo(socket);
            }

        } catch (IOException e) {
            System.out.println("Erro ao processar a conexão do cliente: " + e.getMessage());
        }
    }

}
