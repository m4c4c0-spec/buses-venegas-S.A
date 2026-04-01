package cl.venegas.buses_api.interfaces.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthCall;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/blockchain")
public class BlockchainController {

    @Value("${blockchain.rpc-url}")
    private String rpcUrl;

    @Value("${blockchain.contract-address}")
    private String contractAddress;

    @GetMapping("/verify/{reservaId}/{ticketHash}")
    public ResponseEntity<?> verifyTicket(@PathVariable String reservaId, @PathVariable String ticketHash) {
        if ("0x00".equals(contractAddress) || contractAddress == null) {
            return ResponseEntity.ok(Map.of("valid", false, "message", "Blockchain no configurada"));
        }

        try {
            Web3j web3j = Web3j.build(new HttpService(rpcUrl));

            if (ticketHash.startsWith("0x")) {
                ticketHash = ticketHash.substring(2);
            }
            byte[] hashBytes = new byte[32];
            for (int i = 0; i < 32; i++) {
                hashBytes[i] = (byte) Integer.parseInt(ticketHash.substring(i * 2, i * 2 + 2), 16);
            }

            Function function = new Function(
                    "verifyTicket",
                    Arrays.<Type>asList(new Utf8String(reservaId), new Bytes32(hashBytes)),
                    Arrays.<TypeReference<?>>asList(new TypeReference<org.web3j.abi.datatypes.Bool>() {})
            );

            String encodedFunction = FunctionEncoder.encode(function);
            
            EthCall response = web3j.ethCall(
                    Transaction.createEthCallTransaction(null, contractAddress, encodedFunction),
                    DefaultBlockParameterName.LATEST
            ).send();

            if (response.hasError()) {
                return ResponseEntity.status(500).body(Map.of("valid", false, "error", response.getError().getMessage()));
            }

            String value = response.getValue();
            if (value == null || value.equals("0x")) {
                return ResponseEntity.ok(Map.of("valid", false, "message", "Contrato revertido o vacío"));
            }

            boolean isValid = !value.equals("0x0000000000000000000000000000000000000000000000000000000000000000");

            return ResponseEntity.ok(Map.of("valid", isValid));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("valid", false, "error", e.getMessage()));
        }
    }
}
