const questionsContainer = document.getElementById("questions-container");

// 문제 10개 생성
for (let i = 1; i <= 10; i++) {
  const questionBox = document.createElement("div");
  questionBox.className = "question-box";

  questionBox.innerHTML = `
    <h3>문제 ${i}</h3>
    <label>문제:</label>
    <textarea name="question" rows="2" placeholder="문제를 입력하세요."></textarea>

    <label>문제 이미지 (선택):</label>
    <input type="file" accept="image/*" class="image-upload" />
    <img class="image-preview" style="max-width: 200px; display: none;" />

    <label>선택지 A:</label>
    <input type="text" name="optionA" />
    <label>선택지 B:</label>
    <input type="text" name="optionB" />
    <label>선택지 C:</label>
    <input type="text" name="optionC" />
    <label>선택지 D:</label>
    <input type="text" name="optionD" />

    <label>정답:</label>
    <select name="answer">
      <option value="">선택</option>
      <option value="0">A</option>
      <option value="1">B</option>
      <option value="2">C</option>
      <option value="3">D</option>
    </select>
  `;

  const fileInput = questionBox.querySelector(".image-upload");
  const previewImg = questionBox.querySelector(".image-preview");

  fileInput.addEventListener("change", function () {
    const file = this.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function (e) {
        previewImg.src = e.target.result;
        previewImg.style.display = "block";
      };
      reader.readAsDataURL(file);
    }
  });

  questionsContainer.appendChild(questionBox);
}

// 저장 버튼 클릭 시
document.getElementById("quizForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  const title = document.getElementById("examTitle").value.trim();
  const category = document.getElementById("category").value;
  const creator = document.getElementById("creator").value.trim();

  if (!title || !category || !creator) {
    alert("시험지 제목, 카테고리, 출제자 이름을 입력하세요.");
    return;
  }

  const questionBoxes = document.querySelectorAll(".question-box");
  const questions = [];

  for (let box of questionBoxes) {
    const question = box.querySelector("textarea[name='question']").value.trim();
    const options = [
      box.querySelector("input[name='optionA']").value.trim(),
      box.querySelector("input[name='optionB']").value.trim(),
      box.querySelector("input[name='optionC']").value.trim(),
      box.querySelector("input[name='optionD']").value.trim()
    ];
    const answerIndex = box.querySelector("select[name='answer']").value;

    const file = box.querySelector(".image-upload").files[0];
    let imageBase64 = "";

    if (file) {
      imageBase64 = await readFileAsBase64(file);
    }

    if (!question || options.includes("") || answerIndex === "") {
      alert("모든 문제 내용을 입력하고 정답을 선택해주세요.");
      return;
    }

    questions.push({
      question,
      options,
      answer: options[parseInt(answerIndex)],
      answerIndex: parseInt(answerIndex),
      image: imageBase64
    });
  }

  const examData = {
    title,
    category,
    creator,
    createdAt: new Date().toISOString(),
    questions
  };

  const existing = JSON.parse(localStorage.getItem("exams") || "[]");
  existing.push(examData);
  localStorage.setItem("exams", JSON.stringify(existing));

  alert("시험지가 저장되었습니다!");
  location.reload();
});

function readFileAsBase64(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result);
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
}
