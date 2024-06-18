  	// 날씨 영역
  	var apiURI = "http://api.openweathermap.org/data/2.5/weather?q="+"seoul"+ "&lang=kr" + "&units=metric" + 
  			"&appid="+"be9d839b0ff3435abb7f4e732bcc8649";

    const weatherMap = {
        'Clear': '맑음',
        'Clouds': '구름',
        'Rain': '비',
        'Drizzle': '이슬비',
        'Thunderstorm': '천둥번개',
        'Snow': '눈',
        'Mist': '안개',
        'Smoke': '연기',
        'Haze': '실안개',
        'Dust': '먼지',
        'Fog': '안개',
        'Sand': '모래',
        'Ash': '재',
        'Squall': '돌풍',
        'Tornado': '토네이도'
    };
 	
  	$.ajax({
  	    url: apiURI,
  	    dataType: "json",
  	    type: "GET",
  	    success: function(data) {
  	        var weatherDesc = data.weather[0].main;
  	        var temp = (data.main.temp).toFixed(1);
  	        var city = '서울';

  	      	$('#weather-icon').append('<img src= "http://openweathermap.org/img/wn/'
  	              + data.weather[0].icon + '.png"></img>');
  	        $('#weather-desc').append(weatherMap[weatherDesc]);
  	        $('#temp').append(temp + "°C");
  	        $('#city').append(city);
  	    }
  	})
	  
	  // 출 퇴근 영역
	  let startWorkTime, endWorkTime, workingTime;
	  let strStartTime, strEndTime;
	  let timer;
  	  let hour = 0, minute = 0, second = 0;
  	  let perWork = 0;
  	  let h = 0, m = 0, s = 0;
	  document.addEventListener('DOMContentLoaded', function() {
	      if(localStorage.getItem('startWorkTime') != null){
			startWorkTime = moment(localStorage.getItem('startWorkTime'));
			let currentTime  = moment();
			
			// 시간 차이 계산 (milliseconds)
			let diffMilliseconds = currentTime .diff(startWorkTime);
			
			// duration 객체로 변환하여 시, 분, 초로 접근할 수 있음
			let duration = moment.duration(diffMilliseconds);
			hour = duration.hours();
			minute = duration.minutes();
			second = duration.seconds();
			perWork = (hour * 3600) + (minute * 60) + (second);			
			
		  	$("#work-start").text("출근시간 : " + startWorkTime.format('HH:mm:ss'));			  
		  	$("#btn-work").attr("disabled", true);
		  	$("#btn-finish").attr("disabled", false);
		  	
		  	timer = setInterval(updateAtt, 1000);
		  	updateAtt();
		  }
	  });  	  
  	  
	  function updateAtt() {
		  	++second;
		  	++perWork;
		  	
		  	if(second >= 60){
		  		++minute;
		  		second = 0;
		  	}
		    if(minute >= 60){ 
		        ++hour;
		        minute = 0;
		    }
		    // 숫자를 2자리로 표현
		    h = String(hour).padStart(2, '0');
		    m = String(minute).padStart(2, '0');
		    s = String(second).padStart(2, '0');
		    
		    let perval = ((perWork / 32400) * 100);
		    
		    $(".progress-bar").width(perval + "%");
		    $(".progress-text").text(perval.toFixed(2) + "%"); // 소수점 2자리까지만
		    
		    $("#work-time").text("근무시간 : " + h + ":" + m + ":" + s);	
		  }
		  
		function fnClickWork(){
		  	startWorkTime = moment();
		  	localStorage.setItem('startWorkTime', startWorkTime.toISOString());
		  	strStartTime = startWorkTime.format('HH:mm:ss');
		  	
		  	
		  	$("#work-start").text("출근시간 : " + strStartTime);			  
		  	$("#btn-work").attr("disabled", true);
		  	$("#btn-finish").attr("disabled", false);
		  	
		  	timer = setInterval(updateAtt, 1000);
		  	updateAtt();
		}		  
		  
	  function fnClickFin(){
			localStorage.removeItem('startWorkTime');
		    // 현재 시간을 HH:mm:ss 형식으로 저장
		    endWorkTime = moment().format('HH:mm:ss');

		    // 근무 시간 계산
		    workingTime = h + ":" + m + ":" + s;

		    // 화면에 퇴근 시간 표시 및 버튼 상태 변경
		    $("#work-fin").text("퇴근시간 : " + endWorkTime);			  
		    $("#btn-work").attr("disabled", false);
		    $("#btn-finish").attr("disabled", true);
		    clearInterval(timer);

		    // 현재 날짜를 가져옵니다
		    const currentDate = moment().format('YYYY-MM-DD');

		    // JSON 데이터를 생성합니다
		    const data = JSON.stringify({
		        'attInTime': startWorkTime,
		        'attOutTime': endWorkTime,
		        'attWorkTime': workingTime,
		        'createDt': currentDate,
		        'creator': empNo,
		        'modifyDt': currentDate,
		        'modifier': empNo,
		        'empNo': empNo
		    });

		    $.ajax({
		        type: 'POST',
		        url: '/crispy/registAtt',
		        contentType: 'application/json',
		        dataType: 'json',
		        data: data
		    })
			.done(function(data){
				alert("근태 저장 성공");
			})
			.fail(function(jqXHR){
				alert("근태 저장 실패");
				alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
			})
		}
	    
	  // 차트 영역
  const ctx = document.querySelector('#myChart');
  let guList = [];
  let avgSalesList = [];
  let totalSalesList = [];
  
	$.ajax({
		type:'GET',
		url: '/crispy/getGuAvgSales',
        contentType: 'application/json',
		data:'month=' + 6,
		dataType:'json'
    })
	.done(function(data){
		$(data).each(function(){
			guList.push(this.frnGu);
			avgSalesList.push(this.totalAvgSales);		
			totalSalesList.push(this.totalSales);		
		})
		// alert("매출 성공");
		      new Chart(ctx, {
	          type: 'bar',  // bar, line, pie, doughnut, radar 등등...
	          data: {
	              labels: guList,
	              datasets: [
		            {
		                type: 'bar', // 바 차트 데이터 세트
		                label: '구별 평균 매출',
		                data: avgSalesList,
		                backgroundColor: 'rgba(75, 192, 192, 0.2)',
		                borderColor: 'rgba(75, 192, 192, 1)',
		                borderWidth: 1
		            },
		            {
		                type: 'line', // 라인 차트 데이터 세트
		                label: '구별 총 매출',
		                data: totalSalesList,
		                fill: false,
		                borderColor: 'rgba(153, 102, 255, 1)'
		            }
	              ]
	          },
	          options: {
	              scales: {
	                  y: {
	                      beginAtZero: true
	                  }
	              }
	          }
	      });	  
	})
	.fail(function(jqXHR){
		alert("매출 실패");
		alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
	})    
		
	// 시계
	function updateClock() {
	    const now = new Date();
	    const daysOfWeek = ["일", "월", "화", "수", "목", "금", "토"];
	    const dayOfWeek = daysOfWeek[now.getDay()];
	    const year = now.getFullYear();
	    const month = (now.getMonth() + 1).toString().padStart(2, '0');
	    const day = now.getDate().toString().padStart(2, '0');
	    const hours = now.getHours();
	    const minutes = now.getMinutes().toString().padStart(2, '0');
	    const seconds = now.getSeconds().toString().padStart(2, '0');
	    
	    let ampm = 'AM';
	    let displayHours = hours;
	    
	    if (hours >= 12) {
	        ampm = 'PM';
	        displayHours = hours % 12;
	        if (displayHours === 0) {
	        	displayHours = 12;
	        }
	    }
	    
	    const dateString = year + "년 " + month + "월 " + day + "일 " + "(" + dayOfWeek + ")"; 
	    const timeString = displayHours + ":" + minutes + ":" + seconds + " " + ampm;
	    document.getElementById('date').textContent = dateString;
	    document.getElementById('time').textContent = timeString;
	    
        const secondsDegrees = ((seconds / 60) * 360) + 90;
        document.getElementById('second').style.transform = `rotate(${secondsDegrees}deg)`;

        const minutesDegrees = ((minutes / 60) * 360) + ((seconds/60)*6) + 90;
        document.getElementById('minute').style.transform = `rotate(${minutesDegrees}deg)`;

        const hoursDegrees = ((hours / 12) * 360) + ((minutes/60)*30) + 90;
        document.getElementById('hour').style.transform = `rotate(${hoursDegrees}deg)`;
	}
	
	// 매 초마다 시계 업데이트
	setInterval(updateClock, 1000);
	// 페이지 로드 시에도 시계 업데이트
	updateClock();		
