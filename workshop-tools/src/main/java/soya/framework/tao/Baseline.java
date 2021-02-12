package soya.framework.tao;

/**
 * <p>
 * 道可道也，非恒道也。名可名也，非恒名也。
 * 无名，万物之始也；有名，万物之母也。
 * 故恒无欲也，以观其眇；恒有欲也，以观其所徼。两者同出，异名同谓。玄之又玄，众眇之门。
 * </p>
 * <p>
 * O: 无名
 * K: 有名.
 * </p>
 *
 * @author Wen Qun
 */
public interface Baseline<O, K extends Annotatable> {
    O origin();

    K knowledgeBase();
}
