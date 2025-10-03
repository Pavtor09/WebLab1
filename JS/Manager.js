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
});