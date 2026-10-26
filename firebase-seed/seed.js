// Requires GOOGLE_APPLICATION_CREDENTIALS pointing to a private service account JSON.
// Never commit that JSON. Execute once with: npm install && npm run seed
const admin = require('firebase-admin');
admin.initializeApp({credential:admin.credential.applicationDefault()});
const db=admin.firestore();
const topics=[['t001','Food & Drinks',1],['t002','University Life',2],['t003','Daily Life',3]];
const wordRows=[
['w001','t001','Beverage','/ˈbev.ər.ɪdʒ/','Đồ uống','Water is a healthy beverage.'],
['w002','t001','Delicious','/dɪˈlɪʃ.əs/','Ngon','The soup is delicious.'],
['w003','t001','Ingredient','/ɪnˈɡriː.di.ənt/','Nguyên liệu','We need fresh ingredients.'],
['w004','t001','Recipe','/ˈres.ɪ.pi/','Công thức nấu ăn','This recipe is easy to follow.'],
['w005','t001','Flavor','/ˈfleɪ.vər/','Hương vị','I love the flavor of this tea.'],
['w006','t001','Appetite','/ˈæp.ə.taɪt/','Sự thèm ăn','A walk can improve your appetite.'],
['w007','t001','Nutritious','/nuːˈtrɪʃ.əs/','Bổ dưỡng','Vegetables are nutritious.'],
['w008','t001','Refreshment','/rɪˈfreʃ.mənt/','Đồ ăn hoặc uống nhẹ','Refreshments will be served.'],
['w009','t001','Portion','/ˈpɔːr.ʃən/','Khẩu phần','I ordered a small portion.'],
['w010','t001','Cuisine','/kwɪˈziːn/','Ẩm thực','Vietnamese cuisine is diverse.'],
['w011','t002','Lecture','/ˈlek.tʃər/','Bài giảng','The lecture starts at eight.'],
['w012','t002','Assignment','/əˈsaɪn.mənt/','Bài tập được giao','I finished the assignment.'],
['w013','t002','Semester','/sɪˈmes.tər/','Học kỳ','The semester ends in June.'],
['w014','t002','Scholarship','/ˈskɑː.lər.ʃɪp/','Học bổng','She won a scholarship.'],
['w015','t002','Research','/rɪˈsɜːrtʃ/','Nghiên cứu','We conduct research in the lab.'],
['w016','t002','Campus','/ˈkæm.pəs/','Khuôn viên trường','The campus has a library.'],
['w017','t002','Deadline','/ˈded.laɪn/','Hạn chót','The deadline is tomorrow.'],
['w018','t002','Curriculum','/kəˈrɪk.jə.ləm/','Chương trình học','The curriculum includes design.'],
['w019','t002','Tutorial','/tuːˈtɔːr.i.əl/','Buổi hướng dẫn','The tutorial helped me learn.'],
['w020','t002','Graduate','/ˈɡrædʒ.u.eɪt/','Tốt nghiệp','I hope to graduate next year.'],
['w021','t003','Resilient','/rɪˈzɪl.i.ənt/','Kiên cường','She remained resilient.'],
['w022','t003','Discover','/dɪˈskʌv.ər/','Khám phá','We discover something new.'],
['w023','t003','Routine','/ruːˈtiːn/','Thói quen','Reading is part of my routine.'],
['w024','t003','Improve','/ɪmˈpruːv/','Cải thiện','Practice can improve your English.'],
['w025','t003','Organize','/ˈɔːr.ɡə.naɪz/','Sắp xếp','I organize my desk every day.'],
['w026','t003','Balance','/ˈbæl.əns/','Sự cân bằng','Work-life balance matters.'],
['w027','t003','Explore','/ɪkˈsplɔːr/','Khám phá','We explore the city on weekends.'],
['w028','t003','Prepare','/prɪˈper/','Chuẩn bị','I prepare my bag before class.'],
['w029','t003','Focus','/ˈfoʊ.kəs/','Tập trung','Try to focus on one task.'],
['w030','t003','Reflect','/rɪˈflekt/','Suy ngẫm','I reflect on my progress.']
];
async function run(){const batch=db.batch();for(const [id,title,sortOrder] of topics){batch.set(db.collection('topics').doc(id),{topicId:id,title,sortOrder,description:title});}
for(const [id,topicId,english,phonetic,meaning,example] of wordRows){batch.set(db.collection('words').doc(id),{wordId:id,topicId,english,phonetic,meaning,example,order:Number(id.substring(1))%10});}
await batch.commit();console.log('Seeded 3 topics and 30 words');}run().catch(e=>{console.error(e);process.exit(1)});
