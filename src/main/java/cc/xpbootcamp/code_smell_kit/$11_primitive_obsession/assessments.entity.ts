@Entity({ name: 'assessment' })
export class Assessment {
constructor(assessment: Assessment) {
    Object.assign(this, assessment);
  }
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  @IsNumber()
  @IsNotEmpty()
  @Min(0)
  estCost: number;

  @Column({ nullable: true })
  @IsOptional()
  @IsNumber()
  @Min(0)
  amuEstimated: number;

  // 将实际花费使用字符串存储，整个$和数字
  @Column({ nullable: true })
  @IsOptional()
  @IsString()
  @MaxLength(255)
  actualCost: string;

  // ... other columns
}

 /* ```js
 *  accounting.unformat("£ 12,345,678.90 GBP"); // 12345678.9
 * ```
 *
 * @method unformat
 * @for accounting
 * @param {String|Array<String>} value The string or array of strings containing the number/s to parse.
 * @param {Number}               decimal Number of decimal digits of the resultant number
 * @return {Float} The parsed number
 */
import { unformat } from 'accounting-js';
const unformatCurrency = (assessment: Assessment): void => {
  assessment.estCost = unformat(assessment.estCost);
  assessment.amuEstimated = unformat(assessment.amuEstimated);
};

// 使用unformat方法将传进来的金额字符串，转换成number