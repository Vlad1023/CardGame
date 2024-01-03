// position_cards.js

class CardPositionManagement {
    constructor(offsetXStart, offsetZStart, offsetXChange, offsetZChange, bottomOffset) {
        this.offsetXStart = offsetXStart;
        this.offsetZStart = offsetZStart;
        this.offsetXChange = offsetXChange;
        this.offsetZChange = offsetZChange;
        this.bottomOffset = bottomOffset;
        this.accOffsetX = 0;
        this.accOffsetZ = 0;
    }

    positionCards(models) {
        this.accOffsetX = this.offsetXStart;
        this.accOffsetZ = this.offsetZStart;

        models.forEach((model) => {
            const roundedOffsetX = roundToPrecision(this.accOffsetX, 4);
            const roundedOffsetZ = roundToPrecision(this.accOffsetZ, 4);

            model.position.set(roundedOffsetX, this.bottomOffset, roundedOffsetZ);

            this.accOffsetX += this.offsetXChange;
            this.accOffsetZ += this.offsetZChange;
        });
    }

    placeCardInTheEnd(card) {
        card.position.set(this.accOffsetX, this.bottomOffset, this.accOffsetZ);
        this.accOffsetX += this.offsetXChange;
        this.accOffsetZ += this.offsetZChange;
    }
}

const cardPlayerPositionManagement = new CardPositionManagement(-3, 0.0, 0.02, -0.03, -2.2);
const cardOpponentPositionManagement = new CardPositionManagement(5.5, 0.0, 0.02, -0.03, 2.5);

function roundToPrecision(number, precision) {
    const factor = Math.pow(10, precision);
    return Math.round(number * factor) / factor;
}

export { cardPlayerPositionManagement, cardOpponentPositionManagement };
