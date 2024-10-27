package com.bits.foodie.mappers;

import com.bits.foodie.dto.CardDTO;
import com.bits.foodie.model.Card;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public Card mapCardDTOtoCard(CardDTO cardDTO) {
        Card card = new Card();
        card.setCardId(cardDTO.getCardId());
        card.setCardExpiry(cardDTO.getCardExpiry());
        card.setCardName(cardDTO.getCardName());
        card.setCardNo(cardDTO.getCardNo());
        return card;
    }

    public CardDTO mapCardToCardDTO(Card card) {
        CardDTO cardDTO = new CardDTO();
        cardDTO.setCardId(card.getCardId());
        cardDTO.setCardExpiry(card.getCardExpiry());
        cardDTO.setCardName(card.getCardName());
        cardDTO.setCardNo(card.getCardNo());
        return cardDTO;
    }
}
