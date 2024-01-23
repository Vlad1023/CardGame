// position_cards.js
import * as TWEEN from '@tweenjs/tween.js';
import * as THREE from 'three';

class CardPositionManagement {
    constructor(offsetXStart, offsetZStart, offsetXChange, offsetZChange, bottomOffset, moveXOffsetStart, moveXOffsetChange) {
        this.offsetXStart = offsetXStart;
        this.offsetZStart = offsetZStart;
        this.offsetXChange = offsetXChange;
        this.offsetZChange = offsetZChange;
        this.bottomOffset = bottomOffset;
        this.accOffsetX = 0;
        this.accOffsetZ = 0;

        this.moveXOffsetStart = moveXOffsetStart;
        this.moveXOffsetChange = moveXOffsetChange;

        this.moveXOffsetAccumulated = moveXOffsetStart;

    }

    positionCards(models) {
        this.accOffsetX = this.offsetXStart;
        this.accOffsetZ = this.offsetZStart;
        models.forEach((object) => {
            const roundedOffsetX = roundToPrecision(this.accOffsetX, 4);
            const roundedOffsetZ = roundToPrecision(this.accOffsetZ, 4);
            console.log(`Card position: X=${roundedOffsetX}, Z=${roundedOffsetZ}`);

            const neededPosition = new THREE.Vector3(roundedOffsetX, this.bottomOffset, roundedOffsetZ);

            object.position.copy(neededPosition);

            this.accOffsetX += this.offsetXChange;
            this.accOffsetZ += this.offsetZChange;
        });
    }

    placeCardInTheEnd(card) {
        const roundedOffsetX = roundToPrecision(this.accOffsetX, 4);
        const roundedOffsetZ = roundToPrecision(this.accOffsetZ, 4);

        const targetPosition = new THREE.Vector3(roundedOffsetX, this.bottomOffset, roundedOffsetZ);

        return tweenCardMovementAndRotation(card, targetPosition)
            .then(() => {
                console.log(`Card position: X=${roundedOffsetX}, Z=${roundedOffsetZ}`);
                console.log(`Card position after tween animation: X=${card.position.x}, Z=${card.position.z}`);

//                this.accOffsetX += this.offsetXChange; as we just remove cards from scene, we dont need it
//                this.accOffsetZ += this.offsetZChange;
            });
    }

    placeCardOnScenePosition(){
        const neededXOffset = this.moveXOffsetAccumulated;
        const targetPosition = new THREE.Vector3(neededXOffset, 0, 0);

        this.moveXOffsetAccumulated += this.moveXOffsetChange;
        return targetPosition;
    }

    resetMoveXOffsetAccumulated(){
        this.moveXOffsetAccumulated = this.moveXOffsetStart;
    }
}

// ...

export function tweenCardMovementAndRotation(card, targetPosition) {
    return new Promise((resolve) => {
        const startPosition = Object.assign({}, card.position);

        const positionTween = new TWEEN.Tween(startPosition)
            .to(targetPosition, 1000)
            .onUpdate(() => {
                card.position.copy(startPosition);
            })
            .onComplete(() => {
                // Resolve the position tween
                resolve();
            })
            .start();

        //create new rotation vector with same rotation as card but z + Math.PI
        const newRotation = new THREE.Vector3(card.rotation.x, card.rotation.y, card.rotation.z + Math.PI);

        const rotationTween = new TWEEN.Tween(card.rotation)
            .to(newRotation, 1000)
            .onComplete(() => {
                // Resolve the rotation tween
                resolve();
            })
            .easing(TWEEN.Easing.Linear.None)
            .start();
    });
}

export function initCardDisplayment(card) {
    const scale = 0.2;
    card.scale.setScalar(scale);
    card.rotateOnAxis(new THREE.Vector3(1, 0, 0), Math.PI / 2);
    card.rotateOnAxis(new THREE.Vector3(0, 0, 1), Math.PI);
}





const cardPlayerPositionManagement = new CardPositionManagement(-3, 0.0, 0.01, -0.05, -2.5, -0.6, -0.1);
const cardOpponentPositionManagement = new CardPositionManagement(5, 0.0, 0.01, -0.05, 2.5, 0.8, 0.1);

function roundToPrecision(number, precision) {
    const factor = Math.pow(10, precision);
    return Math.round(number * factor) / factor;
}

export { cardPlayerPositionManagement, cardOpponentPositionManagement };
