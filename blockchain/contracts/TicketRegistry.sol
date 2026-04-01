// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

/**
 * @title TicketRegistry
 * @dev Contrato para registrar de forma inmutable la validez de los boletos emitidos.
 */
contract TicketRegistry {
    address public owner;

    // Mapeo de ID de Reserva => Hash del boleto
    mapping(string => bytes32) private tickets;

    event TicketRegistered(string indexed reservaId, bytes32 indexed ticketHash, uint256 timestamp);

    modifier onlyOwner() {
        require(msg.sender == owner, "Solo el sistema backend puede registrar boletos");
        _;
    }

    constructor() {
        owner = msg.sender;
    }

    /**
     * @dev Registra un boleto nuevo en la blockchain.
     * @param reservaId ID unico de la reserva (ej. BB123456)
     * @param ticketHash Hash SHA-256 de los datos comprobables del boleto
     */
    function registerTicket(string memory reservaId, bytes32 ticketHash) public onlyOwner {
        require(tickets[reservaId] == bytes32(0), "El boleto ya esta registrado en la blockchain");
        
        tickets[reservaId] = ticketHash;
        
        emit TicketRegistered(reservaId, ticketHash, block.timestamp);
    }

    /**
     * @dev Verifica si un hash presentado por un cliente es valido y genuino para esa reserva.
     * @param reservaId ID de la reserva a consultar
     * @param ticketHash El hash que esta escrito en el PDF o presentado por el pasajero
     * @return bool True si el hash coincide exactamente con el registrado bloqueando fraudes
     */
    function verifyTicket(string memory reservaId, bytes32 ticketHash) public view returns (bool) {
        bytes32 storedHash = tickets[reservaId];
        require(storedHash != bytes32(0), "Boleto inexistente en blockchain");
        return storedHash == ticketHash;
    }

    /**
     * @dev Obtiene el hash almacenado para auditorias publicas.
     * @param reservaId ID de la reserva
     */
    function getTicketHash(string memory reservaId) public view returns (bytes32) {
        return tickets[reservaId];
    }
}
