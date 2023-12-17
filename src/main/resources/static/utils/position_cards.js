export function positionBottom(models) {
  const offsetXStart = -3;
  const offsetZStart = 0.0;
  const offsetXChange = 0.02;
  const offsetZChange = -0.03;
  const bottomOffset = -2.2;
  let accOffsetX = offsetXStart;
  let accOffsetZ = offsetZStart;
  models.forEach((model, index) => {
    const roundedOffsetX = roundToPrecision(accOffsetX, 4);
    const roundedOffsetZ = roundToPrecision(accOffsetZ, 4);

    model.position.set(roundedOffsetX, bottomOffset, roundedOffsetZ);


    accOffsetX += offsetXChange;
    accOffsetZ += offsetZChange;
  });
}

export function positionTop(models) {
  const offsetXStart = 5.5;
  const offsetZStart = 0.0;
  const offsetXChange = 0.02;
  const offsetZChange = -0.03;
  const bottomOffset = 2.5;
  let accOffsetX = offsetXStart;
  let accOffsetZ = offsetZStart;
  models.forEach((model, index) => {
    const roundedOffsetX = roundToPrecision(accOffsetX, 4);
    const roundedOffsetZ = roundToPrecision(accOffsetZ, 4);

    model.position.set(roundedOffsetX, bottomOffset, roundedOffsetZ);


    accOffsetX += offsetXChange;
    accOffsetZ += offsetZChange;
  });
}


function roundToPrecision(number, precision) {
  const factor = Math.pow(10, precision);
  return Math.round(number * factor) / factor;
}
