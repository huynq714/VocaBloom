package com.vocabloom.app.data;
import com.vocabloom.app.model.Word;
import java.util.*;
public final class SampleData {
 private SampleData() {}
 public static final String[] TOPICS={"Food & Drinks","University Life","Daily Life","IT","Environment"};
 private static final List<Word> WORDS=Arrays.asList(
 new Word("w001",TOPICS[0],"Beverage","/ˈbev.ər.ɪdʒ/","Đồ uống","Water is a healthy beverage."),
 new Word("w002",TOPICS[0],"Delicious","/dɪˈlɪʃ.əs/","Ngon","The soup is delicious."),
 new Word("w003",TOPICS[0],"Ingredient","/ɪnˈɡriː.di.ənt/","Nguyên liệu","We need fresh ingredients."),
 new Word("w004",TOPICS[0],"Recipe","/ˈres.ɪ.pi/","Công thức nấu ăn","This recipe is easy to follow."),
 new Word("w005",TOPICS[0],"Flavor","/ˈfleɪ.vər/","Hương vị","I love the flavor of this tea."),
 new Word("w006",TOPICS[0],"Appetite","/ˈæp.ə.taɪt/","Sự thèm ăn","A walk can improve your appetite."),
 new Word("w007",TOPICS[0],"Nutritious","/nuːˈtrɪʃ.əs/","Bổ dưỡng","Vegetables are nutritious."),
 new Word("w008",TOPICS[0],"Refreshment","/rɪˈfreʃ.mənt/","Đồ ăn hoặc uống nhẹ","Refreshments will be served."),
 new Word("w009",TOPICS[0],"Portion","/ˈpɔːr.ʃən/","Khẩu phần","I ordered a small portion."),
 new Word("w010",TOPICS[0],"Cuisine","/kwɪˈziːn/","Ẩm thực","Vietnamese cuisine is diverse."),
 new Word("w011",TOPICS[1],"Lecture","/ˈlek.tʃər/","Bài giảng","The lecture starts at eight."),
 new Word("w012",TOPICS[1],"Assignment","/əˈsaɪn.mənt/","Bài tập được giao","I finished the assignment."),
 new Word("w013",TOPICS[1],"Semester","/sɪˈmes.tər/","Học kỳ","The semester ends in June."),
 new Word("w014",TOPICS[1],"Scholarship","/ˈskɑː.lər.ʃɪp/","Học bổng","She won a scholarship."),
 new Word("w015",TOPICS[1],"Research","/rɪˈsɜːrtʃ/","Nghiên cứu","We conduct research in the lab."),
 new Word("w016",TOPICS[1],"Campus","/ˈkæm.pəs/","Khuôn viên trường","The campus has a library."),
 new Word("w017",TOPICS[1],"Deadline","/ˈded.laɪn/","Hạn chót","The deadline is tomorrow."),
 new Word("w018",TOPICS[1],"Curriculum","/kəˈrɪk.jə.ləm/","Chương trình học","The curriculum includes design."),
 new Word("w019",TOPICS[1],"Tutorial","/tuːˈtɔːr.i.əl/","Buổi hướng dẫn","The tutorial helped me learn."),
 new Word("w020",TOPICS[1],"Graduate","/ˈɡrædʒ.u.eɪt/","Tốt nghiệp","I hope to graduate next year."),
 new Word("w021",TOPICS[2],"Resilient","/rɪˈzɪl.i.ənt/","Kiên cường","She remained resilient."),
 new Word("w022",TOPICS[2],"Discover","/dɪˈskʌv.ər/","Khám phá","We discover something new."),
 new Word("w023",TOPICS[2],"Routine","/ruːˈtiːn/","Thói quen","Reading is part of my routine."),
 new Word("w024",TOPICS[2],"Improve","/ɪmˈpruːv/","Cải thiện","Practice can improve your English."),
 new Word("w025",TOPICS[2],"Organize","/ˈɔːr.ɡə.naɪz/","Sắp xếp","I organize my desk every day."),
 new Word("w026",TOPICS[2],"Balance","/ˈbæl.əns/","Sự cân bằng","Work-life balance matters."),
 new Word("w027",TOPICS[2],"Explore","/ɪkˈsplɔːr/","Khám phá","We explore the city on weekends."),
 new Word("w028",TOPICS[2],"Prepare","/prɪˈper/","Chuẩn bị","I prepare my bag before class."),
 new Word("w029",TOPICS[2],"Focus","/ˈfoʊ.kəs/","Tập trung","Try to focus on one task."),
 new Word("w030",TOPICS[2],"Reflect","/rɪˈflekt/","Suy ngẫm","I reflect on my progress."),
 new Word("w031",TOPICS[3],"Algorithm","/\u02C8\u00E6l.\u0261\u0259.r\u026A.\u00F0\u0259m/","Thu\u1EADt to\u00E1n","The algorithm sorts the results quickly."),
 new Word("w032",TOPICS[3],"Database","/\u02C8de\u026A.t\u0259.be\u026As/","C\u01A1 s\u1EDF d\u1EEF li\u1EC7u","The app stores each word in a database."),
 new Word("w033",TOPICS[3],"Encryption","/\u026An\u02C8kr\u026Ap.\u0283\u0259n/","S\u1EF1 m\u00E3 h\u00F3a","Encryption helps protect user data."),
 new Word("w034",TOPICS[3],"Interface","/\u02C8\u026An.t\u0259.fe\u026As/","Giao di\u1EC7n","The new interface is easy to use."),
 new Word("w035",TOPICS[3],"Network","/\u02C8net.w\u025C\u02D0k/","M\u1EA1ng l\u01B0\u1EDBi","The devices connect to a wireless network."),
 new Word("w036",TOPICS[3],"Software","/\u02C8s\u0252ft.we\u0259r/","Ph\u1EA7n m\u1EC1m","She develops software for schools."),
 new Word("w037",TOPICS[3],"Hardware","/\u02C8h\u0251\u02D0d.we\u0259r/","Ph\u1EA7n c\u1EE9ng","The technician checked the computer hardware."),
 new Word("w038",TOPICS[3],"Debug","/di\u02D0\u02C8b\u028C\u0261/","G\u1EE1 l\u1ED7i","We need to debug the login screen."),
 new Word("w039",TOPICS[3],"Cloud computing","/\u02CCkla\u028Ad k\u0259m\u02C8pju\u02D0.t\u026A\u014B/","\u0110i\u1EC7n to\u00E1n \u0111\u00E1m m\u00E2y","Cloud computing lets teams share resources."),
 new Word("w040",TOPICS[3],"Artificial intelligence","/\u02CC\u0251\u02D0.t\u026A.f\u026A.\u0283\u0259l \u026An\u02C8tel.\u026A.d\u0292\u0259ns/","Tr\u00ED tu\u1EC7 nh\u00E2n t\u1EA1o","Artificial intelligence can recognize speech."),
 new Word("w041",TOPICS[4],"Biodiversity","/\u02CCba\u026A.\u0259\u028A.da\u026A\u02C8v\u025C\u02D0.s\u026A.ti/","\u0110a d\u1EA1ng sinh h\u1ECDc","Healthy forests support biodiversity."),
 new Word("w042",TOPICS[4],"Conservation","/\u02CCk\u0252n.s\u0259\u02C8ve\u026A.\u0283\u0259n/","S\u1EF1 b\u1EA3o t\u1ED3n","Wildlife conservation protects endangered species."),
 new Word("w043",TOPICS[4],"Ecosystem","/\u02C8i\u02D0.k\u0259\u028A\u02CCs\u026As.t\u0259m/","H\u1EC7 sinh th\u00E1i","Every species plays a role in its ecosystem."),
 new Word("w044",TOPICS[4],"Emissions","/\u026A\u02C8m\u026A.\u0283\u0259nz/","Kh\u00ED th\u1EA3i","Public transport can reduce carbon emissions."),
 new Word("w045",TOPICS[4],"Renewable","/r\u026A\u02C8nju\u02D0.\u0259.b\u0259l/","C\u00F3 th\u1EC3 t\u00E1i t\u1EA1o","Solar power is a renewable energy source."),
 new Word("w046",TOPICS[4],"Sustainable","/s\u0259\u02C8ste\u026A.n\u0259.b\u0259l/","B\u1EC1n v\u1EEFng","The city is investing in sustainable transport."),
 new Word("w047",TOPICS[4],"Pollution","/p\u0259\u02C8lu\u02D0.\u0283\u0259n/","\u00D4 nhi\u1EC5m","Plastic pollution harms marine life."),
 new Word("w048",TOPICS[4],"Drought","/dra\u028At/","H\u1EA1n h\u00E1n","The long drought affected local farms."),
 new Word("w049",TOPICS[4],"Habitat","/\u02C8h\u00E6b.\u026A.t\u00E6t/","M\u00F4i tr\u01B0\u1EDDng s\u1ED1ng","The wetlands are a habitat for many birds."),
 new Word("w050",TOPICS[4],"Recycling","/\u02CCri\u02D0\u02C8sa\u026A.kl\u026A\u014B/","S\u1EF1 t\u00E1i ch\u1EBF","Recycling paper saves natural resources."));

 public static List<Word> byTopic(String topic) { List<Word> out=new ArrayList<>();for(Word w:WORDS)if(w.topicId.equals(topic))out.add(w);return out; }
 public static List<Word> all() {return new ArrayList<>(WORDS);}
 public static Word byId(String id) { for(Word w:WORDS)if(w.id.equals(id))return w; return null; }
}
