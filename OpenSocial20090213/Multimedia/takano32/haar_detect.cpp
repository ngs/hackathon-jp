#include <stdio.h>
#include <cv.h>
#include <highgui.h>

int
main (int argc, char **argv)
{
	int i;
	int gui = true;
	IplImage *src_img = 0, *src_gray = 0;
	const char *cascade_name = "haarcascade_frontalface_default.xml";
	CvHaarClassifierCascade *cascade = 0;
	CvMemStorage *storage = 0;
	CvSeq *faces;
	static CvScalar colors[] = {
		{{0, 0, 255}}, {{0, 128, 255}},
		{{0, 255, 255}}, {{0, 255, 0}},
		{{255, 128, 0}}, {{255, 255, 0}},
		{{255, 0, 0}}, {{255, 0, 255}}
	};

	// (1)画像を読み込む
	if (argc < 2 || (src_img = cvLoadImage (argv[1], CV_LOAD_IMAGE_COLOR)) == 0)
		return -1;
	if (argc == 3 && strcmp("--no-gui", argv[2]) == 0 )
		gui = false;
	
	src_gray = cvCreateImage (cvGetSize (src_img), IPL_DEPTH_8U, 1);

	// (2)ブーストされた分類器のカスケードを読み込む
	cascade = (CvHaarClassifierCascade *) cvLoad (cascade_name, 0, 0, 0);

	// (3)メモリを確保し，読み込んだ画像のグレースケール化，ヒストグラムの均一化を行う
	storage = cvCreateMemStorage (0);
	cvClearMemStorage (storage);
	cvCvtColor (src_img, src_gray, CV_BGR2GRAY);
	cvEqualizeHist (src_gray, src_gray);

	// (4)物体（顔）検出
	faces = cvHaarDetectObjects (src_gray, cascade, storage, 1.11, 4, 0, cvSize (40, 40));


	// puts("<faces>");
	printf("[{\"faces\":");
	// (5)検出された全ての顔位置に，円を描画する
	for (i = 0; i < (faces ? faces->total : 0); i++) {
		// puts("  <face>");
		
		CvRect *r = (CvRect *) cvGetSeqElem (faces, i);
		CvPoint center;
		int radius;
 		// printf("    <top>%d</top>\n", r->y);
 		// printf("    <right>%d</right>\n", r->x + r->width);
 		// printf("    <bottom>%d</bottom>\n", r->y + r->height);
 		// printf("    <left>%d</left>\n", r->x);
		puts("[{");
		printf("	\"id\":%d,\n", 0);
		printf("	\"x\":%d,\n", r->x);
		printf("	\"y\":%d,\n", r->y);
		printf("	\"w\":%d,\n", r->width);
		printf("	\"h\":%d\n", r->height);
		puts("}]");
		if (i != faces->total - 1) {
			puts(",");
		}
		center.x = cvRound (r->x + r->width * 0.5);
		center.y = cvRound (r->y + r->height * 0.5);
		radius = cvRound ((r->width + r->height) * 0.25);
		cvCircle (src_img, center, radius, colors[i % 8], 3, 8, 0);
		// puts("  </face>");
	}
	// puts("</faces>");
	puts("}]");





	// (6)画像を表示，キーが押されたときに終了
	if (gui) {
		cvNamedWindow ("Face Detection", CV_WINDOW_AUTOSIZE);
		cvShowImage ("Face Detection", src_img);
		cvWaitKey (0);
	}

	cvDestroyWindow ("Face Detection");
	cvReleaseImage (&src_img);
	cvReleaseImage (&src_gray);
	cvReleaseMemStorage (&storage);

	return 0;
}
