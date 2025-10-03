document.querySelector('.input-form').addEventListener('submit', function(e) {
  e.preventDefault(); // Отключаем стандартную отправку формы

  // Получаем все отмеченные чекбоксы X
  const xValues = Array.from(document.querySelectorAll('input[name="x"]:checked'))
    .map(cb => cb.value);

  // Получаем значение Y
  const yValue = document.querySelector('input[name="y"]').value;

  // Получаем выбранное значение R
  const rValue = document.querySelector('input[name="r"]:checked')?.value;

  // Пример вывода в консоль
  console.log('X:', xValues);
  console.log('Y:', yValue);
  console.log('R:', rValue);

  
  // Здесь можно дальше использовать эти значения для построения графика
drawFigure(rValue);
});

function drawFigure(rawR) {
    
  const upscale = 60;
  let R = rawR * upscale
  const canvas = document.getElementById('myCanvas');
  const ctx = canvas.getContext('2d');
  ctx.clearRect(0, 0, canvas.width, canvas.height);


  // Центр координат
  const cx = canvas.width / 2;
  const cy = canvas.height / 2;

  ctx.fillStyle = '#379cff';

  // Рисуем четверть круга (дугу)
  ctx.beginPath();
  ctx.moveTo(cx, cy);
  ctx.arc(cx, cy, R, Math.PI * 1.5,0, false);
  ctx.lineTo(cx, cy);
  ctx.closePath();
  ctx.fill();

  // Рисуем прямоугольник слева внизу
  ctx.beginPath();
  ctx.moveTo(cx, cy);
  ctx.rect(cx-R, cy, R, R / 2);
  ctx.closePath();
  ctx.fill();

  // Рисуем треугольник слева вверху
  ctx.beginPath();
  ctx.moveTo(cx, cy);
  ctx.lineTo(cx - R/2, cy);
  ctx.lineTo(cx, cy-R);
  ctx.closePath();
  ctx.fill();

  // Оси координат
  ctx.strokeStyle = "black";
  ctx.lineWidth = 2;

  // x
  ctx.beginPath();
  ctx.moveTo(0, cy);
  ctx.lineTo(canvas.width, cy);
  ctx.stroke();

  // y
  ctx.beginPath();
  ctx.moveTo(cx, 0);
  ctx.lineTo(cx, canvas.height);
  ctx.stroke();

  // Подписи R, R/2
  ctx.fillStyle = "#000";
  ctx.font = "12px serif";
  ctx.fillText(-rawR, cx - R, cy + 12);
  ctx.fillText(rawR, cx + R - 15, cy + 12);
  ctx.fillText(rawR/2, cx - R / 2, cy + 12);
  ctx.fillText(-rawR/2, cx + R / 2 - 15, cy + 12);

  ctx.fillText(-rawR, cx + 2, cy + R);
  ctx.fillText(rawR, cx + 2, cy - R + 12);
  ctx.fillText(rawR/2, cx + 2, cy - R / 2 + 12);
  ctx.fillText(-rawR/2, cx + 2, cy + R / 2 + 12);
}
drawFigure();