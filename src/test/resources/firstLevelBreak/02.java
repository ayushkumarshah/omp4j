class FirstLevelBreakExtractorTest02 {
	public static void main(String[] args) {
		int i =0;
		// omp parallel for
		for (; i < 10; i++) {

			for (;;) {
				break;
			}

			while (true) {
				break;
			}

			break;
		}
	}
}
