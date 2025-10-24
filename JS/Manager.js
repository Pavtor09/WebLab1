document.querySelector('.input-form').addEventListener('submit', function(e) {
  e.preventDefault(); // Отключаем стандартную отправку формы
  // Показываем стандартную ошибку формы если данные невалидны
  // Получаем значение Y
  const yValue = document.querySelector('input[name="y"]').value;

  // Получаем выбранное значение R
  const rValue = document.querySelector('input[name="r"]:checked')?.value;

  // Проверка именно интервала (-5; 5)
  if (isNaN(yValue) || yValue <= -5 || yValue >= 5) {
    alert("Y должен быть внутри интервала (-5; 5)");
    return;
  }

  // Получаем все отмеченные чекбоксы X
  const xValues = Array.from(document.querySelectorAll('input[name="x"]:checked'))
    .map(cb => cb.value);

  // Получаем значение Y
  // const yValue = document.querySelector('input[name="y"]').value;

  // // Получаем выбранное значение R
  // const rValue = document.querySelector('input[name="r"]:checked')?.value;

  // Пример вывода в консоль
  console.log('X:', xValues);
  console.log('Y:', yValue);
  console.log('R:', rValue);

  
  // Здесь можно дальше использовать эти значения для построения графика
drawFigure(rValue);
  Send();
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
  
  drawGrid(ctx, cx, cy, upscale, 0.5, canvas.width, canvas.height);
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
  
  if (rawR > 0){
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
}

function drawGrid(ctx, cx, cy, scale, step, canvasWidth, canvasHeight) {
  ctx.save(); // Сохраняем текущие настройки
  ctx.strokeStyle = "#cccccc"; // Цвет сетки
  ctx.lineWidth = 1;

  // Вертикальные линии
  for (let x = cx; x <= canvasWidth; x += step * scale) {
    ctx.beginPath();
    ctx.moveTo(x, 0);
    ctx.lineTo(x, canvasHeight);
    ctx.stroke();
  }
  for (let x = cx - step * scale; x >= 0; x -= step * scale) {
    ctx.beginPath();
    ctx.moveTo(x, 0);
    ctx.lineTo(x, canvasHeight);
    ctx.stroke();
  }

  // Горизонтальные линии
  for (let y = cy; y <= canvasHeight; y += step * scale) {
    ctx.beginPath();
    ctx.moveTo(0, y);
    ctx.lineTo(canvasWidth, y);
    ctx.stroke();
  }
  for (let y = cy - step * scale; y >= 0; y -= step * scale) {
    ctx.beginPath();
    ctx.moveTo(0, y);
    ctx.lineTo(canvasWidth, y);
    ctx.stroke();
  }


  ctx.strokeStyle = "#000";
  ctx.lineWidth = 2;

  // Отметки на оси X
  for (let x = -canvasWidth; x <= canvasWidth; x += step * scale) {
    ctx.beginPath();
    ctx.moveTo(x, cy - 5); // верхняя точка палочки
    ctx.lineTo(x, cy + 5); // нижняя точка палочки
    ctx.stroke();
  }

  // Отметки на оси Y
  for (let y = -canvasHeight; y <= canvasHeight; y += step * scale) {
    ctx.beginPath();
    ctx.moveTo(cx - 5, y); // левая точка палочки
    ctx.lineTo(cx + 5, y); // правая точка палочки
    ctx.stroke();
  }

  ctx.restore();
}



window.onload = function() {
  drawFigure(0);
const rows = JSON.parse(localStorage.getItem("tableRows")) || [];
  renderTable(rows);
}

  document.querySelectorAll('input[name="r"]').forEach(radio => {
    radio.addEventListener('change', function() {
        if (this.checked) {
            drawFigure(document.querySelector('input[name="r"]:checked')?.value);
        }
    });
});
  

// function Send()
// {
//    const xValues = Array.from(document.querySelectorAll('input[name="x"]:checked')).map(cb => cb.value);

//     // Получаем значение Y (input type="number")
//     const yValue = document.querySelector('input[name="y"]').value;

//     // Получаем выбранное значение R (радиокнопки)
//     const RValue = document.querySelector('input[name="r"]:checked')?.value;
//   const data = {x: xValues, y: yValue, R: RValue}
//   fetch('/calculate', {
//   method: 'POST',
//   headers: { 'Content-Type': 'application/json' },
//   body: JSON.stringify(data)
// })
// .then(response => {
//     if (!response.ok) throw new Error('Ошибка сети: ' + response.status)
//     else data => populateTable(data);
//     return response.json();
// })
// .catch(error => console.error('Ошибка:', error));

// }

function Send() {
    const xValues = Array.from(document.querySelectorAll('input[name="x"]:checked')).map(cb => cb.value);
    const yValue = document.querySelector('input[name="y"]').value;
    const RValue = document.querySelector('input[name="r"]:checked')?.value;

    const data = {x: xValues, y: yValue, R: RValue};

    fetch('/calculate', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
    })
    .then(response => {
         if (!response.ok) {
      // Попытка получить текст ошибки
      return response.text().then(text => {
        // Показываем ошибку пользователю (можно alert или блок)
        alert(`Ошибка: ${response.status} ${response.statusText}\n${text}`);
        throw new Error(text || 'Ошибка сервера');
      });
    }
        return response.json();
      
    })
    .then(data => {
        populateTable(data);
    })
    .catch(error => console.error('Ошибка:', error));
}

function populateTable(data) {

  
  const tbody = document.querySelector('.myTable tbody');
  // tbody.innerHTML = ''; // очищаем таблицу перед заполнением
  let rows = JSON.parse(localStorage.getItem("tableRows")) || [];
  data.x.forEach((xVal, index) => {
    rows.push({
      x: xVal,
      y: data.y,
      R: data.R,
      Hit: data.Hit ? data.Hit[index] : '',
      Time: getCurrentTimeString(),
      Ex_Time: data.Ex_Time
    });
  });

  // data.x.forEach((xVal, index) => {
  //   const row = document.createElement('tr');

  //   // x - массив, берем из data.x по индексам
  //   const xCell = document.createElement('td');
  //   xCell.textContent = xVal; // или, если нужно, можно вывести все x через запятую
  //   row.appendChild(xCell);

  //   // y
  //   const yCell = document.createElement('td');
  //   yCell.textContent = data.y; // предполагается, что y — число или строка
  //   row.appendChild(yCell);

  //   // R
  //   const RCell = document.createElement('td');
  //   RCell.textContent = data.R;
  //   row.appendChild(RCell);

  //   // Hit - массив булевых, берем по индексу или выводим весь массив
  //   const hitCell = document.createElement('td');
  //   hitCell.textContent = data.Hit ? (data.Hit[index] ? 'true' : 'false') : '';
  //   row.appendChild(hitCell);

  //   // Time
  //   const timeCell = document.createElement('td');
  //   timeCell.textContent = getCurrentTimeString(); // передать как строку
  //   row.appendChild(timeCell);

  //   // executionTime
  //   const execCell = document.createElement('td');
  //   execCell.textContent = data.Ex_Time;
  //   row.appendChild(execCell);

  //   tbody.appendChild(row);
  // });
    // сохраняем обновлённые строки обратно
  localStorage.setItem("tableRows", JSON.stringify(rows));

  // отображаем все строки
  renderTable(rows);
}

function clearTable()
{
  const tbody = document.querySelector('.myTable tbody');
  tbody.innerHTML = ''; // очищаем таблицу перед заполнением
  localStorage.removeItem("tableRows");
  renderTable([]);
}

function getCurrentTimeString() {
  const now = new Date();

  const pad = (num) => num.toString().padStart(2, '0');

  const hours = pad(now.getHours());
  const minutes = pad(now.getMinutes());
  const seconds = pad(now.getSeconds());

  return `${hours}:${minutes}:${seconds}`;
}

document.getElementById('clear').addEventListener('click', () => {
clearTable();
});

function renderTable(rows) {
  const tbody = document.querySelector('.myTable tbody');
  tbody.innerHTML = '';
  rows.forEach(row => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${row.x}</td>
      <td>${row.y}</td>
      <td>${row.R}</td>
      <td>${row.Hit === true ? 'true' : row.Hit === false ? 'false' : ''}</td>
      <td>${row.Time}</td>
      <td>${row.Ex_Time}</td>
    `;
    tbody.appendChild(tr);
  });
}

