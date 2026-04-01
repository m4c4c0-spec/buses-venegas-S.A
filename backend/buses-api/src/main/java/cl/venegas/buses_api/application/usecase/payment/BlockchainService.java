package cl.venegas.buses_api.application.usecase.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;

import jakarta.annotation.PostConstruct;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;

@Service
public class BlockchainService {

    @Value("${blockchain.rpc-url}")
    private String rpcUrl;

    @Value("${blockchain.private-key}")
    private String privateKey;

    @Value("${blockchain.contract-address}")
    private String contractAddress;

    private Web3j web3j;
    private Credentials credentials;
    private TransactionManager transactionManager;

    @PostConstruct
    public void init() {
        if (rpcUrl != null && !rpcUrl.isEmpty() && !rpcUrl.contains("BLOCKCHAIN_RPC_URL")) {
            this.web3j = Web3j.build(new HttpService(rpcUrl));
            this.credentials = Credentials.create(privateKey);
            long chainId = 11155111; // Sepolia Chain ID
            this.transactionManager = new RawTransactionManager(web3j, credentials, chainId);
            System.out.println("✅ Web3j conectado exitosamente a Infura (Sepolia). Wallet Backend OK.");
        } else {
            System.out.println("⚠️ Variables blockchain vacías. Integración Blockchain inactiva en este inicio.");
        }
    }

    /**
     * Genera un hash inmutable usando SHA-256 en base a datos reales del boleto
     */
    public String generateTicketHash(String reservaId, String origen, String destino, String fecha, String pasajerosJson) {
        try {
            String rawData = reservaId + "|" + origen + "|" + destino + "|" + fecha + "|" + pasajerosJson;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(rawData.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Envía la transacción al smart contract para registrar el boleto en Sepolia
     */
    public String registerTicketOnChain(String reservaId, String hexHash) {
        if (this.web3j == null || "0x00".equals(contractAddress)) {
            return "Blockchain deshabilitada localmente";
        }

        try {
            // Convierte el hex string a un array de 32 bytes para Solidity
            if (hexHash.startsWith("0x")) {
                hexHash = hexHash.substring(2);
            }
            byte[] hashBytes = new byte[32];
            for (int i = 0; i < 32; i++) {
                hashBytes[i] = (byte) Integer.parseInt(hexHash.substring(i * 2, i * 2 + 2), 16);
            }

            // Preparar la funcion ABI de Solidty: registerTicket(string,bytes32)
            Function function = new Function(
                    "registerTicket",
                    Arrays.<Type>asList(new Utf8String(reservaId), new Bytes32(hashBytes)),
                    Collections.<TypeReference<?>>emptyList()
            );

            String encodedFunction = FunctionEncoder.encode(function);

            // Configuracion de gas para Sepolia
            BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
            StaticGasProvider gasProvider = new StaticGasProvider(gasPrice, BigInteger.valueOf(300000));

            // Enviar transaccion
            org.web3j.protocol.core.methods.response.EthSendTransaction ethSendTransaction = transactionManager.sendTransaction(
                    gasProvider.getGasPrice(), 
                    gasProvider.getGasLimit(encodedFunction), 
                    contractAddress, 
                    encodedFunction, 
                    BigInteger.ZERO
            );

            if (ethSendTransaction.hasError()) {
                throw new RuntimeException("Revertido: " + ethSendTransaction.getError().getMessage());
            }

            String txHash = ethSendTransaction.getTransactionHash();
            System.out.println("✅ Boleto " + reservaId + " registrado en Sepolia Blockchain! Tx Hash: " + txHash);
            return txHash;

        } catch (Exception e) {
            System.err.println("❌ Error registrando ticket en blockchain: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
