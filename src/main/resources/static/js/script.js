
console.log("This is script file")


const toggleSidebar=()=>{
	if($(".sidebar").is(":visible"))
	{
	$(".sidebar").css("display","none");
	$(".content").css("margin-left","0%");	
	}else
	{
	$(".sidebar").css("display","block");
	$(".content").css("margin-left","20%");	
	}
	
};

const search=()=>
{
	//console.log("searching....");
	
	let query=$("#search-input").val();
	if(query=='')
	{
		$(".search-result").hide();
	}
	else
	{
		//sending request to server
		
		let url='http://localhost:8080/search/'+query;
		fetch(url)
		.then((response)=>{
			return response.json();	
		})
		.then((data)=>{
			//data....
			
			let text="<div class='list-group'>";
			let name='';
			let id='';
			data.forEach((contact)=>{
				 name=contact.name;
				 id=contact.cId;
				text+="<a href='/user/"+id+"/contact' class='list-group-item list-group-action'>"+name+"</a>"
			});
			text+="</div>";

			$(".search-result").html(text);
			$(".search-result").show();		
			
		});
			
	}
	
	
	
	
};
